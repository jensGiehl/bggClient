package de.agiehl.bgg.http;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlMapper;
import de.agiehl.bgg.config.BggClientConfig;
import de.agiehl.bgg.exception.BggAuthenticationException;
import de.agiehl.bgg.exception.BggClientException;
import de.agiehl.bgg.exception.BggHttpException;
import de.agiehl.bgg.exception.BggParseException;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thin wrapper around {@link HttpClient} that handles BGG-specific concerns:
 *
 * <ul>
 *     <li>injects the configured API key on every call,</li>
 *     <li>stores an optional website login session and sends matching cookies,</li>
 *     <li>parses XML responses into typed model classes,</li>
 *     <li>retries on rate-limit ({@code 429}, {@code 503}) and async
 *         ({@code 202}) responses according to
 *         {@link BggClientConfig#getMaxRetries()} and
 *         {@link BggClientConfig#getRetryBackoff()},</li>
 *     <li>maps non-success responses to {@link BggHttpException}.</li>
 * </ul>
 *
 * <p>All retry attempts, request URIs and failure modes are logged through
 * {@link java.util.logging.Logger} so that integrators can wire the executor
 * into their preferred JUL configuration.
 *
 * <p>This class is thread-safe: both {@link HttpClient} and {@link XmlMapper}
 * are designed to be shared. Callers should reuse the same executor across
 * requests, which is what {@link de.agiehl.bgg.BggClient} does.
 */
public class HttpExecutor {

    private static final Logger LOGGER = Logger.getLogger(HttpExecutor.class.getName());
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private static final int HTTP_ACCEPTED = 202;
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final int HTTP_SERVICE_UNAVAILABLE = 503;

    private final BggClientConfig config;
    private final HttpClient httpClient;
    private final HttpClient loginHttpClient;
    private final XmlMapper xmlMapper;

    /**
     * Creates a new executor backed by a freshly built {@link HttpClient}
     * configured from {@code config}.
     *
     * @param config the client configuration, never {@code null}
     */
    public HttpExecutor(BggClientConfig config) {
        this(config, new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
    }

    private HttpExecutor(BggClientConfig config, CookieManager cookies) {
        this(config, createHttpClient(config, cookies, HttpClient.Redirect.NORMAL),
                createHttpClient(config, cookies, HttpClient.Redirect.NEVER), XmlMapperFactory.create());
    }

    private static HttpClient createHttpClient(BggClientConfig config, CookieManager cookies,
                                             HttpClient.Redirect redirects) {
        return HttpClient.newBuilder()
                .connectTimeout(config.getConnectTimeout())
                .version(HttpClient.Version.HTTP_1_1)
                .cookieHandler(cookies)
                .followRedirects(redirects)
                .build();
    }

    /**
     * Creates a new executor with caller-supplied {@link HttpClient} and
     * {@link XmlMapper} instances. Intended for tests and advanced
     * customisation.
     *
     * <p>Website login requires a dedicated {@link CookieManager} on the supplied
     * client and {@link HttpClient.Redirect#NEVER} to prevent forwarding credentials.
     *
     * @param config     the client configuration
     * @param httpClient the HTTP client to use
     * @param xmlMapper  the XML mapper to use
     */
    public HttpExecutor(BggClientConfig config, HttpClient httpClient, XmlMapper xmlMapper) {
        this(config, httpClient, httpClient, xmlMapper);
    }

    private HttpExecutor(BggClientConfig config, HttpClient httpClient, HttpClient loginHttpClient,
                         XmlMapper xmlMapper) {
        this.config = config;
        this.httpClient = httpClient;
        this.loginHttpClient = loginHttpClient;
        this.xmlMapper = xmlMapper;
    }

    /**
     * Logs in to the website at the configured API origin. Cookies are kept in
     * memory for this executor and automatically included in matching requests,
     * including retries. Credentials are used only for this request.
     *
     * <p>Each login request replaces the previous session. A failed request clears all
     * cookies so subsequent requests cannot accidentally use another account.
     *
     * @param username the website username, must not be blank
     * @param password the website password, must not be empty
     * @throws BggAuthenticationException if the login fails or returns no authentication cookies
     * @throws IllegalStateException if a custom HTTP client does not support safe cookie handling
     */
    public synchronized void login(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        CookieManager cookies = loginCookies();
        cookies.getCookieStore().removeAll();
        try {
            URI loginUri = config.getBaseUri().resolve("/login/api/v1");
            byte[] body = JSON_MAPPER.writeValueAsBytes(Map.of("credentials",
                    Map.of("username", username, "password", password)));
            HttpRequest request = HttpRequest.newBuilder(loginUri)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .version(HttpClient.Version.HTTP_1_1)
                    .timeout(config.getRequestTimeout())
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Accept", "application/json")
                    .header("User-Agent", config.getUserAgent())
                    .build();
            HttpResponse<byte[]> response = send(request, loginHttpClient);
            int status = response.statusCode();
            if (status < 200 || status >= 300 || status == HTTP_ACCEPTED) {
                throw new BggAuthenticationException("BGG login failed (HTTP " + status + ")");
            }
            URI apiUri = buildUri("/collection", QueryParameters.create());
            if (!hasAuthenticationCookie(cookies, apiUri, "bggusername")
                    || !hasAuthenticationCookie(cookies, apiUri, "bggpassword")) {
                throw new BggAuthenticationException("BGG login did not return valid authentication cookies");
            }
        } catch (RuntimeException exception) {
            cookies.getCookieStore().removeAll();
            throw exception;
        }
    }

    private CookieManager loginCookies() {
        if (loginHttpClient.followRedirects() != HttpClient.Redirect.NEVER) {
            throw new IllegalStateException("Website login requires an HTTP client that does not follow redirects");
        }
        return loginHttpClient.cookieHandler()
                .filter(CookieManager.class::isInstance)
                .map(CookieManager.class::cast)
                .orElseThrow(() -> new IllegalStateException("Website login requires an HTTP client with a CookieManager"));
    }

    private static boolean hasAuthenticationCookie(CookieManager cookies, URI apiUri, String name) {
        return cookies.getCookieStore().get(apiUri).stream()
                .filter(cookie -> name.equals(cookie.getName()))
                .filter(cookie -> !cookie.hasExpired())
                .filter(cookie -> !cookie.getSecure() || "https".equalsIgnoreCase(apiUri.getScheme()))
                .anyMatch(cookie -> !cookie.getValue().isBlank() && !"deleted".equalsIgnoreCase(cookie.getValue())
                        && matchesPath(cookie, apiUri));
    }

    private static boolean matchesPath(HttpCookie cookie, URI uri) {
        String path = cookie.getPath();
        String requestPath = uri.getPath();
        return path == null || path.equals(requestPath)
                || requestPath.startsWith(path.endsWith("/") ? path : path + "/");
    }

    /**
     * Executes a {@code GET} request against the given path with the supplied
     * query parameters and deserializes the response body into {@code type}.
     *
     * @param path           the endpoint path, e.g. {@code "/thing"}
     * @param queryParameters the parameters to send, never {@code null}
     * @param type           the target type
     * @param <T>            the response type
     * @return the deserialized response body
     * @throws BggHttpException  if the server returns a non-success status
     * @throws BggParseException if the response cannot be parsed
     */
    public <T> T get(String path, QueryParameters queryParameters, Class<T> type) {
        URI uri = buildUri(path, queryParameters);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .timeout(config.getRequestTimeout())
                .header("Accept", "application/xml, text/xml")
                .header("User-Agent", config.getUserAgent())
                .header("Authorization", "Bearer " + config.getApiKey())
                .build();

        LOGGER.log(Level.FINE, "GET {0}", uri);
        HttpResponse<byte[]> response = sendWithRetries(request);
        if (LOGGER.isLoggable(Level.FINER)) {
            int size = response.body() == null ? 0 : response.body().length;
            LOGGER.log(Level.FINER, "Received HTTP {0} ({1} bytes) from {2}",
                    new Object[] {response.statusCode(), size, uri});
        }
        return parse(response.body(), type);
    }

    private HttpResponse<byte[]> sendWithRetries(HttpRequest request) {
        int attempt = 0;
        while (true) {
            HttpResponse<byte[]> response = send(request);
            int status = response.statusCode();

            if (status >= 200 && status < 300 && status != HTTP_ACCEPTED) {
                return response;
            }

            if (isRetryable(status) && attempt < config.getMaxRetries()) {
                attempt++;
                long backoffMs = config.getRetryBackoff().toMillis();
                LOGGER.log(Level.WARNING,
                        "BGG returned HTTP {0} for {1}; retrying ({2}/{3}) after {4} ms",
                        new Object[] {status, request.uri(), attempt, config.getMaxRetries(), backoffMs});
                sleepQuietly(backoffMs);
                continue;
            }

            String body = response.body() == null ? "" : new String(response.body(), StandardCharsets.UTF_8);
            LOGGER.log(Level.WARNING,
                    "BGG request to {0} failed with HTTP {1} after {2} retr{3}",
                    new Object[] {request.uri(), status, attempt, attempt == 1 ? "y" : "ies"});
            throw new BggHttpException(status, body);
        }
    }

    private static boolean isRetryable(int statusCode) {
        return statusCode == HTTP_ACCEPTED
                || statusCode == HTTP_TOO_MANY_REQUESTS
                || statusCode == HTTP_SERVICE_UNAVAILABLE;
    }

    private HttpResponse<byte[]> send(HttpRequest request) {
        return send(request, httpClient);
    }

    private HttpResponse<byte[]> send(HttpRequest request, HttpClient client) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e, () -> "I/O failure while contacting " + request.uri());
            throw new BggClientException("I/O failure while contacting " + request.uri(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Interrupted while contacting {0}", request.uri());
            throw new BggClientException("Interrupted while contacting " + request.uri(), e);
        }
    }

    private <T> T parse(byte[] body, Class<T> type) {
        try {
            return xmlMapper.readValue(body, type);
        } catch (JacksonException e) {
            LOGGER.log(Level.WARNING, e, () -> "Failed to parse response as " + type.getSimpleName());
            throw new BggParseException("Failed to parse response as " + type.getSimpleName(), e);
        }
    }

    private URI buildUri(String path, QueryParameters queryParameters) {
        QueryParameters effective = queryParameters == null ? QueryParameters.create() : queryParameters;
        
        String base = config.getBaseUri().toString();
        String fullPath = path.startsWith("/") ? path : "/" + path;
        return URI.create(base + fullPath + effective.toQueryString());
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
