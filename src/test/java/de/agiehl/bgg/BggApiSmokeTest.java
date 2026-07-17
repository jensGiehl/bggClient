package de.agiehl.bgg;

import de.agiehl.bgg.config.BggClientConfig;
import de.agiehl.bgg.exception.BggClientException;
import de.agiehl.bgg.exception.BggHttpException;
import de.agiehl.bgg.model.collection.CollectionResponse;
import de.agiehl.bgg.model.common.ForumListType;
import de.agiehl.bgg.model.family.FamilyResponse;
import de.agiehl.bgg.model.forum.ForumListResponse;
import de.agiehl.bgg.model.forum.ForumResponse;
import de.agiehl.bgg.model.forum.ThreadResponse;
import de.agiehl.bgg.model.guild.GuildResponse;
import de.agiehl.bgg.model.hot.HotResponse;
import de.agiehl.bgg.model.plays.PlaysResponse;
import de.agiehl.bgg.model.search.SearchResponse;
import de.agiehl.bgg.model.thing.ThingResponse;
import de.agiehl.bgg.model.user.UserResponse;
import de.agiehl.bgg.request.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end smoke test against the live BoardGameGeek XML API.
 *
 * <p>Disabled unless the {@code BGG_API_KEY} environment variable is set.
 * Every other input (ids, usernames, search query) has a sensible default and
 * can be overridden via environment variables — see the {@code env(...)}
 * helpers below.
 */
@EnabledIfEnvironmentVariable(named = "BGG_API_KEY", matches = ".+")
class BggApiSmokeTest {

    private static final Logger LOGGER = Logger.getLogger(BggApiSmokeTest.class.getName());
    private static final int MAX_ATTEMPTS = envInt("BGG_SMOKE_MAX_ATTEMPTS", 3);
    private static final Duration RETRY_BACKOFF = Duration.ofSeconds(
            envInt("BGG_SMOKE_RETRY_BACKOFF_SECONDS", 5));

    private static BggClient client;

    @BeforeAll
    static void setUp() {
        Duration requestTimeout = Duration.ofSeconds(
                envInt("BGG_SMOKE_REQUEST_TIMEOUT_SECONDS", 60));
        BggClientConfig config = BggClientConfig.withApiKey(System.getenv("BGG_API_KEY"))
                .toBuilder()
                .requestTimeout(requestTimeout)
                .build();
        client = BggClient.builder().config(config).build();
        LOGGER.log(Level.INFO,
                "Starting BGG API smoke tests with Java {0}; baseUri={1}, requestTimeout={2}, "
                        + "maxAttempts={3}, retryBackoff={4}",
                new Object[] {
                        Runtime.version(), client.getConfig().getBaseUri(),
                        client.getConfig().getRequestTimeout(), MAX_ATTEMPTS, RETRY_BACKOFF
                });
    }

    @Test
    void thingEndpointReturnsData() {
        int id = envInt("BGG_THING_ID", 13); // Catan
        ThingResponse response = withRetry("thing", () -> client.things().fetch(
                ThingRequest.builder()
                        .id(id)
                        .versions(true)
                        .videos(true)
                        .stats(true)
                        .marketplace(true)
                        .comments(true)
                        .build()));
        assertNotNull(response, "ThingResponse must not be null");
        assertNotEmpty(response.getItems(), "ThingResponse.items");
    }

    @Test
    void thingEndpointWithRatingCommentsReturnsData() {
        int id = envInt("BGG_THING_ID", 13); // Catan
        ThingResponse response = withRetry("thing-rating-comments", () -> client.things().fetch(
                ThingRequest.builder()
                        .id(id)
                        .ratingComments(true)
                        .build()));
        assertNotNull(response, "ThingResponse must not be null");
        assertNotEmpty(response.getItems(), "ThingResponse.items");
    }

    @Test
    void familyEndpointReturnsData() {
        int id = envInt("BGG_FAMILY_ID", 8374); // Sample family used in BGG client examples
        FamilyResponse response = withRetry("family", () -> client.families().fetchById(id));
        assertNotNull(response, "FamilyResponse must not be null");
        assertNotEmpty(response.getItems(), "FamilyResponse.items");
    }

    @Test
    void forumListEndpointReturnsData() {
        int id = envInt("BGG_FORUM_LIST_ID", 13); // Catan's forum list
        ForumListResponse response = withRetry("forum-list", () -> client.forumLists().fetch(
                ForumListRequest.builder().id(id).type(ForumListType.THING).build()));
        assertNotNull(response, "ForumListResponse must not be null");
        assertNotEmpty(response.getForums(), "ForumListResponse.forums");
    }

    @Test
    void forumEndpointReturnsData() {
        int id = envInt("BGG_FORUM_ID", 26); // Used in the project's own javadoc examples
        ForumResponse response = withRetry("forum", () -> client.forums().fetchById(id));
        assertNotNull(response, "ForumResponse must not be null");
        assertNotEmpty(response.getThreads(), "ForumResponse.threads");
    }

    @Test
    void threadEndpointReturnsData() {
        int id = envInt("BGG_THREAD_ID", 2571698); // Example thread from public BGG clients
        ThreadResponse response = withRetry("thread", () -> client.threads().fetch(
                ThreadRequest.builder().id(id).count(1).build()));
        assertNotNull(response, "ThreadResponse must not be null");
        assertNotEmpty(response.getArticles(), "ThreadResponse.articles");
    }

    @Test
    void userEndpointReturnsData() {
        String name = env("BGG_USERNAME", "Aldie"); // BGG co-founder, stable test user
        UserResponse response = withRetry("user", () -> client.users().fetchByName(name));
        assertNotNull(response, "UserResponse must not be null");
        assertNotNull(response.getName(), "UserResponse.name must not be null");
        assertTrue(response.getName().equalsIgnoreCase(name),
                "Returned username '" + response.getName() + "' does not match requested '" + name + "'");
    }

    @Test
    void guildEndpointReturnsData() {
        int id = envInt("BGG_GUILD_ID", 1000); // Example guild from public BGG clients
        GuildResponse response = withRetry("guild", () -> client.guilds().fetch(
                GuildRequest.builder().id(id).members(true).build()));
        assertNotNull(response, "GuildResponse must not be null");
        assertNotNull(response.getName(), "GuildResponse.name must not be null");
    }

    @Test
    void playsEndpointReturnsData() {
        String name = env("BGG_PLAYS_USERNAME", env("BGG_USERNAME", "Aldie"));
        PlaysResponse response = withRetry("plays", () -> client.plays().fetchByUsername(name));
        assertNotNull(response, "PlaysResponse must not be null");
        assertNotEmpty(response.getPlays(), "PlaysResponse.plays");
    }

    @Test
    void collectionEndpointReturnsData() {
        String name = env("BGG_PLAYS_USERNAME", env("BGG_USERNAME", "Aldie"));
        CollectionResponse response = withRetry("collection", () -> client.collections().fetch(CollectionRequest.builder()
                .username(name)
                .version(true)
                .brief(true)
                .stats(true)
                .owned(true)
                .build()));
        assertNotNull(response, "CollectionResponse must not be null");
        assertNotEmpty(response.getItems(), "CollectionResponse.items");
    }

    @Test
    void hotEndpointReturnsData() {
        HotResponse response = withRetry("hot", () -> client.hot().fetchBoardgames());
        assertNotNull(response, "HotResponse must not be null");
        assertNotEmpty(response.getItems(), "HotResponse.items");
    }

    @Test
    void searchEndpointReturnsData() {
        String query = env("BGG_SEARCH_QUERY", "Catan");
        SearchResponse response = withRetry("search", () -> client.search().search(query));
        assertNotNull(response, "SearchResponse must not be null");
        assertNotEmpty(response.getItems(), "SearchResponse.items");
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static int envInt(String name, int fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : Integer.parseInt(value);
    }

    private static void assertNotEmpty(List<?> list, String field) {
        assertNotNull(list, field + " must not be null");
        assertFalse(list.isEmpty(), field + " must not be empty");
    }

    private static <T> T withRetry(String endpoint, Supplier<T> request) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            long started = System.nanoTime();
            LOGGER.log(Level.INFO, "Calling {0} endpoint (attempt {1}/{2})",
                    new Object[] {endpoint, attempt, MAX_ATTEMPTS});
            try {
                T response = request.get();
                LOGGER.log(Level.INFO, "{0} endpoint succeeded on attempt {1}/{2} after {3} ms",
                        new Object[] {endpoint, attempt, MAX_ATTEMPTS, elapsedMillis(started)});
                return response;
            } catch (BggClientException exception) {
                boolean retry = attempt < MAX_ATTEMPTS && isRetryable(exception);
                LOGGER.log(retry ? Level.WARNING : Level.SEVERE,
                        "{0} endpoint failed on attempt {1}/{2} after {3} ms: {4}: {5}",
                        new Object[] {
                                endpoint, attempt, MAX_ATTEMPTS, elapsedMillis(started),
                                exception.getClass().getSimpleName(), exception.getMessage()
                        });
                if (!retry) {
                    LOGGER.log(Level.SEVERE, "Final failure for " + endpoint + " endpoint", exception);
                    throw exception;
                }
                LOGGER.log(Level.INFO, "Retrying {0} endpoint after {1}",
                        new Object[] {endpoint, RETRY_BACKOFF});
                sleepBeforeRetry();
            }
        }
        throw new IllegalStateException("Unreachable retry state for " + endpoint);
    }

    private static boolean isRetryable(BggClientException exception) {
        if (!(exception instanceof BggHttpException httpException)) {
            return true;
        }
        int status = httpException.getStatusCode();
        return status == 408 || status == 429 || status >= 500;
    }

    private static long elapsedMillis(long started) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);
    }

    private static void sleepBeforeRetry() {
        try {
            Thread.sleep(RETRY_BACKOFF.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting to retry smoke test", exception);
        }
    }

}
