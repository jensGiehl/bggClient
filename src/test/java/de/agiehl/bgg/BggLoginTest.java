package de.agiehl.bgg;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.agiehl.bgg.config.BggClientConfig;
import de.agiehl.bgg.exception.BggAuthenticationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BggLoginTest {

    private static final String LOGIN_PATH = "/login/api/v1";
    private static final String API_PATH = "/xmlapi2";
    private static final String TOKEN = "test-token";
    private static final String USER_AGENT = "login-integration-test/1.0";
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private final List<CapturedRequest> requests = new CopyOnWriteArrayList<>();
    private HttpServer server;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void websiteLoginIsOptional() {
        serve(API_PATH + "/thing", 200, "<items/>");

        var response = client().things().fetchById(13);

        assertNotNull(response);
        assertEquals(1, requests.size());
        var request = requests.getFirst();
        assertEquals("GET", request.method());
        assertEquals("Bearer " + TOKEN, request.header("Authorization"));
        assertEquals(USER_AGENT, request.header("User-Agent"));
        assertTrue(request.cookies().isEmpty());
    }

    @Test
    void postsUtf8JsonCredentialsToWebsiteOriginWithoutApiToken() {
        serve(LOGIN_PATH, 204, "", authenticationCookies("account"));
        String username = "Jörg \"Spieler\"\\Name\n";
        String password = "päss\\word\"\t\n";

        client().login(username, password);

        assertEquals(1, requests.size());
        var request = requests.getFirst();
        assertEquals("POST", request.method());
        assertEquals(LOGIN_PATH, request.uri().getPath());
        assertEquals(null, request.uri().getRawQuery());
        assertEquals("application/json; charset=UTF-8", request.header("Content-Type"));
        assertEquals(USER_AGENT, request.header("User-Agent"));
        assertEquals("", request.header("Authorization"));
        assertTrue(request.cookies().isEmpty());
        assertEquals(Map.of("credentials", Map.of("username", username, "password", password)),
                JSON_MAPPER.readValue(request.body(), Map.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201, 204})
    void acceptsSuccessfulLoginWithAuthenticationCookies(int status) {
        serve(LOGIN_PATH, status, "", authenticationCookies("account"));
        serve(API_PATH + "/hot", 200, "<items/>");
        var client = client();

        client.login("account", "secret");
        client.hot().fetchBoardgames();

        assertEquals("account", requests.getLast().cookies().get("bggusername"));
        assertEquals("authenticated", requests.getLast().cookies().get("bggpassword"));
    }

    @Test
    void sendsCookiesAcrossEndpointsAndUsesUpdatesOnAsyncRetries() {
        serve(LOGIN_PATH, 204, "", "bggusername=account; Path=/", "bggpassword=authenticated; Path=/",
                "SessionID=initial; Path=/");
        var attempts = new AtomicInteger();
        handle(API_PATH + "/collection", exchange -> {
            if (attempts.incrementAndGet() == 1) {
                reply(exchange, 202, "<message>Queued</message>", "SessionID=renewed; Path=/");
            } else {
                reply(exchange, 200, "<items/>");
            }
        });
        serve(API_PATH + "/search", 200, "<items/>");
        serve(API_PATH + "/thing", 200, "<items/>");
        var client = client();

        client.login("account", "secret");
        client.collections().fetchByUsername("account");
        client.search().search("Catan");
        client.things().fetchById(13);

        var apiRequests = requests.stream().filter(request -> request.method().equals("GET")).toList();
        assertEquals(4, apiRequests.size());
        assertEquals("initial", apiRequests.getFirst().cookies().get("SessionID"));
        for (var request : apiRequests) {
            assertEquals("account", request.cookies().get("bggusername"));
            assertEquals("authenticated", request.cookies().get("bggpassword"));
            assertEquals("Bearer " + TOKEN, request.header("Authorization"));
            assertEquals(USER_AGENT, request.header("User-Agent"));
        }
        assertTrue(apiRequests.stream().skip(1)
                .allMatch(request -> "renewed".equals(request.cookies().get("SessionID"))));
    }

    @Test
    void respectsCookiePathSecureExpiryAndOriginalServerDomain() {
        serve(LOGIN_PATH, 204, "", "bggusername=account; Path=/", "bggpassword=authenticated; Path=/",
                "collectionOnly=scoped; Path=/xmlapi2/collection", "secureOnly=hidden; Path=/; Secure",
                "expired=old; Path=/; Max-Age=0", "foreign=hidden; Domain=example.org; Path=/");
        serve(API_PATH + "/collection", 200, "<items/>", "bggpassword=authenticated; Path=/",
                "collectionOnly=removed; Path=/xmlapi2/collection; Max-Age=0");
        serve(API_PATH + "/search", 200, "<items/>");
        var client = client();

        client.login("account", "secret");
        client.collections().fetchByUsername("account");
        client.search().search("Catan");
        client.collections().fetchByUsername("account");

        var apiRequests = requests.stream().filter(request -> request.method().equals("GET")).toList();
        assertEquals("scoped", apiRequests.getFirst().cookies().get("collectionOnly"));
        assertFalse(apiRequests.get(1).cookies().containsKey("collectionOnly"));
        assertFalse(apiRequests.getLast().cookies().containsKey("collectionOnly"));
        for (var request : apiRequests) {
            assertFalse(request.cookies().containsKey("secureOnly"));
            assertFalse(request.cookies().containsKey("expired"));
            assertFalse(request.cookies().containsKey("foreign"));
        }
    }

    @Test
    void keepsCookieSessionsIsolatedBetweenClientInstances() {
        var logins = new AtomicInteger();
        handle(LOGIN_PATH, exchange -> reply(exchange, 204, "",
                authenticationCookies(logins.incrementAndGet() == 1 ? "alice" : "bob")));
        serve(API_PATH + "/hot", 200, "<items/>");
        var alice = client();
        var bob = client();
        var anonymous = client();

        alice.login("alice", "alice-secret");
        bob.login("bob", "bob-secret");
        alice.hot().fetchBoardgames();
        bob.hot().fetchBoardgames();
        anonymous.hot().fetchBoardgames();

        var apiRequests = requests.stream().filter(request -> request.method().equals("GET")).toList();
        assertEquals("alice", apiRequests.getFirst().cookies().get("bggusername"));
        assertEquals("bob", apiRequests.get(1).cookies().get("bggusername"));
        assertTrue(apiRequests.getLast().cookies().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {202, 401, 403, 500})
    void rejectsUnsuccessfulLoginWithoutRetryingAndClearsResponseCookies(int status) {
        String password = "private-password";
        serve(LOGIN_PATH, status, password, authenticationCookies("account"));
        serve(API_PATH + "/hot", 200, "<items/>");
        var client = client();

        var exception = assertThrows(BggAuthenticationException.class,
                () -> client.login("account", password));
        client.hot().fetchBoardgames();

        assertEquals(2, requests.size());
        assertFalse(exception.getMessage().contains(password));
        assertTrue(requests.getLast().cookies().isEmpty());
        assertEquals("Bearer " + TOKEN, requests.getLast().header("Authorization"));
    }

    @ParameterizedTest
    @MethodSource("invalidAuthenticationCookies")
    void rejectsSuccessResponseWithoutUsableAuthenticationCookies(List<String> cookies) {
        serve(LOGIN_PATH, 204, "", cookies.toArray(String[]::new));
        serve(API_PATH + "/hot", 200, "<items/>");
        var client = client();

        assertThrows(BggAuthenticationException.class, () -> client.login("account", "secret"));
        client.hot().fetchBoardgames();

        assertTrue(requests.getLast().cookies().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 401})
    void failedReloginDoesNotReusePreviousSession(int status) {
        var logins = new AtomicInteger();
        handle(LOGIN_PATH, exchange -> {
            if (logins.incrementAndGet() == 1) {
                reply(exchange, 204, "", authenticationCookies("alice"));
            } else {
                reply(exchange, status, "", "SessionID=rejected; Path=/");
            }
        });
        serve(API_PATH + "/hot", 200, "<items/>");
        var client = client();
        client.login("alice", "alice-secret");

        assertThrows(BggAuthenticationException.class, () -> client.login("bob", "bob-secret"));
        client.hot().fetchBoardgames();

        assertEquals(3, requests.size());
        assertTrue(requests.get(1).cookies().isEmpty());
        assertTrue(requests.getLast().cookies().isEmpty());
    }

    @Test
    void doesNotFollowLoginRedirects() {
        handle(LOGIN_PATH, exchange -> {
            exchange.getResponseHeaders().set("Location", "/redirected-login");
            reply(exchange, 307, "", authenticationCookies("account"));
        });
        serve("/redirected-login", 204, "", authenticationCookies("account"));
        serve(API_PATH + "/hot", 200, "<items/>");
        var client = client();

        assertThrows(BggAuthenticationException.class, () -> client.login("account", "secret"));
        client.hot().fetchBoardgames();

        assertEquals(2, requests.size());
        assertTrue(requests.stream().noneMatch(request -> request.uri().getPath().equals("/redirected-login")));
        assertTrue(requests.getLast().cookies().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidCredentials")
    void rejectsInvalidCredentialsBeforeMakingRequest(String username, String password) {
        serve(LOGIN_PATH, 204, "", authenticationCookies("account"));

        assertThrows(IllegalArgumentException.class, () -> client().login(username, password));

        assertTrue(requests.isEmpty());
    }

    @Test
    void preservesWhitespacePassword() {
        serve(LOGIN_PATH, 204, "", authenticationCookies("account"));

        client().login("account", "   ");

        assertEquals(Map.of("credentials", Map.of("username", "account", "password", "   ")),
                JSON_MAPPER.readValue(requests.getFirst().body(), Map.class));
    }

    private BggClient client() {
        var config = BggClientConfig.builder()
                .apiKey(TOKEN)
                .baseUri(URI.create("http://127.0.0.1:" + server.getAddress().getPort() + API_PATH))
                .connectTimeout(Duration.ofSeconds(2))
                .requestTimeout(Duration.ofSeconds(3))
                .userAgent(USER_AGENT)
                .retryBackoff(Duration.ZERO)
                .maxRetries(1)
                .build();
        return BggClient.builder().config(config).build();
    }

    private void serve(String path, int status, String body, String... cookies) {
        handle(path, exchange -> reply(exchange, status, body, cookies));
    }

    private void handle(String path, HttpHandler handler) {
        server.createContext(path, exchange -> {
            requests.add(new CapturedRequest(exchange.getRequestMethod(), exchange.getRequestURI(),
                    Map.copyOf(exchange.getRequestHeaders()),
                    new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)));
            handler.handle(exchange);
        });
    }

    private static void reply(HttpExchange exchange, int status, String body, String... cookies) throws IOException {
        for (String cookie : cookies) {
            exchange.getResponseHeaders().add("Set-Cookie", cookie);
        }
        if (status == 204 || body.isEmpty()) {
            exchange.sendResponseHeaders(status, -1);
            exchange.close();
            return;
        }
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/xml; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static String[] authenticationCookies(String username) {
        return new String[] {"bggusername=" + username + "; Path=/", "bggpassword=authenticated; Path=/"};
    }

    private static Stream<Arguments> invalidAuthenticationCookies() {
        return Stream.of(
                List.<String>of(),
                List.of("SessionID=anonymous; Path=/"),
                List.of("bggusername=account; Path=/"),
                List.of("bggpassword=authenticated; Path=/"),
                List.of("bggusername=; Path=/", "bggpassword=authenticated; Path=/"),
                List.of("bggusername=account; Path=/", "bggpassword=; Path=/"),
                List.of("bggusername=account; Path=/; Max-Age=0", "bggpassword=authenticated; Path=/"),
                List.of("bggusername=account; Path=/login", "bggpassword=authenticated; Path=/login"),
                List.of("bggusername=account; Path=/; Secure", "bggpassword=authenticated; Path=/; Secure")
        ).map(Arguments::of);
    }

    private static Stream<Arguments> invalidCredentials() {
        return Stream.of(Arguments.of(null, "secret"), Arguments.of("", "secret"),
                Arguments.of(" \t", "secret"), Arguments.of("account", null), Arguments.of("account", ""));
    }

    private record CapturedRequest(String method, URI uri, Map<String, List<String>> headers, String body) {

        String header(String name) {
            return headers.entrySet().stream()
                    .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                    .flatMap(entry -> entry.getValue().stream())
                    .reduce((first, second) -> first + "; " + second)
                    .orElse("");
        }

        Map<String, String> cookies() {
            var cookies = new LinkedHashMap<String, String>();
            for (String entry : header("Cookie").split(";")) {
                int separator = entry.indexOf('=');
                if (separator > 0) {
                    cookies.put(entry.substring(0, separator).strip(),
                            entry.substring(separator + 1).strip().replace("\"", ""));
                }
            }
            return cookies;
        }
    }
}
