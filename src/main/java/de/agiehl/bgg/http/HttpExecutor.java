package de.agiehl.bgg.http;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.agiehl.bgg.config.BggClientConfig;
import de.agiehl.bgg.exception.BggClientException;
import de.agiehl.bgg.exception.BggHttpException;
import de.agiehl.bgg.exception.BggParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thin wrapper around {@link HttpClient} that handles BGG-specific concerns:
 *
 * <ul>
 *     <li>injects the configured API key on every call,</li>
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

    private static final int HTTP_ACCEPTED = 202;
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final int HTTP_SERVICE_UNAVAILABLE = 503;

    private final BggClientConfig config;
    private final HttpClient httpClient;
    private final XmlMapper xmlMapper;

    /**
     * Creates a new executor backed by a freshly built {@link HttpClient}
     * configured from {@code config}.
     *
     * @param config the client configuration, never {@code null}
     */
    public HttpExecutor(BggClientConfig config) {
        this(config, HttpClient.newBuilder()
                .connectTimeout(config.getConnectTimeout())
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build(),
                XmlMapperFactory.create());
    }

    /**
     * Creates a new executor with caller-supplied {@link HttpClient} and
     * {@link XmlMapper} instances. Intended for tests and advanced
     * customisation.
     *
     * @param config     the client configuration
     * @param httpClient the HTTP client to use
     * @param xmlMapper  the XML mapper to use
     */
    public HttpExecutor(BggClientConfig config, HttpClient httpClient, XmlMapper xmlMapper) {
        this.config = config;
        this.httpClient = httpClient;
        this.xmlMapper = xmlMapper;
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
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
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
        } catch (IOException e) {
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