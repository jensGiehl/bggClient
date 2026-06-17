package de.agiehl.bgg;

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
import de.agiehl.bgg.request.CollectionRequest;
import de.agiehl.bgg.request.ForumListRequest;
import de.agiehl.bgg.request.GuildRequest;
import de.agiehl.bgg.request.ThingRequest;
import de.agiehl.bgg.request.ThreadRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private static BggClient client;

    @BeforeAll
    static void setUp() {
        client = BggClient.of(System.getenv("BGG_API_KEY"));
    }

    @Test
    void thingEndpointReturnsData() {
        int id = envInt("BGG_THING_ID", 13); // Catan
        ThingResponse response = client.things().fetch(
                ThingRequest.builder().id(id).stats(true).build());
        assertNotNull(response, "ThingResponse must not be null");
        assertNotEmpty(response.getItems(), "ThingResponse.items");
    }

    @Test
    void familyEndpointReturnsData() {
        int id = envInt("BGG_FAMILY_ID", 8374); // Sample family used in BGG client examples
        FamilyResponse response = client.families().fetchById(id);
        assertNotNull(response, "FamilyResponse must not be null");
        assertNotEmpty(response.getItems(), "FamilyResponse.items");
    }

    @Test
    void forumListEndpointReturnsData() {
        int id = envInt("BGG_FORUM_LIST_ID", 13); // Catan's forum list
        ForumListResponse response = client.forumLists().fetch(
                ForumListRequest.builder().id(id).type(ForumListType.THING).build());
        assertNotNull(response, "ForumListResponse must not be null");
        assertNotEmpty(response.getForums(), "ForumListResponse.forums");
    }

    @Test
    void forumEndpointReturnsData() {
        int id = envInt("BGG_FORUM_ID", 26); // Used in the project's own javadoc examples
        ForumResponse response = client.forums().fetchById(id);
        assertNotNull(response, "ForumResponse must not be null");
        assertNotEmpty(response.getThreads(), "ForumResponse.threads");
    }

    @Test
    void threadEndpointReturnsData() {
        int id = envInt("BGG_THREAD_ID", 2571698); // Example thread from public BGG clients
        ThreadResponse response = client.threads().fetch(
                ThreadRequest.builder().id(id).count(1).build());
        assertNotNull(response, "ThreadResponse must not be null");
        assertNotEmpty(response.getArticles(), "ThreadResponse.articles");
    }

    @Test
    void userEndpointReturnsData() {
        String name = env("BGG_USERNAME", "Aldie"); // BGG co-founder, stable test user
        UserResponse response = client.users().fetchByName(name);
        assertNotNull(response, "UserResponse must not be null");
        assertNotNull(response.getName(), "UserResponse.name must not be null");
        assertTrue(response.getName().equalsIgnoreCase(name),
                "Returned username '" + response.getName() + "' does not match requested '" + name + "'");
    }

    @Test
    void guildEndpointReturnsData() {
        int id = envInt("BGG_GUILD_ID", 1000); // Example guild from public BGG clients
        GuildResponse response = client.guilds().fetch(
                GuildRequest.builder().id(id).build());
        assertNotNull(response, "GuildResponse must not be null");
        assertNotNull(response.getName(), "GuildResponse.name must not be null");
    }

    @Test
    void playsEndpointReturnsData() {
        String name = env("BGG_PLAYS_USERNAME", env("BGG_USERNAME", "Aldie"));
        PlaysResponse response = client.plays().fetchByUsername(name);
        assertNotNull(response, "PlaysResponse must not be null");
        assertNotEmpty(response.getPlays(), "PlaysResponse.plays");
    }

    @Test
    void collectionEndpointReturnsData() {
        String name = env("BGG_COLLECTION_USERNAME", env("BGG_USERNAME", "Aldie"));
        CollectionResponse response = client.collections().fetch(
                CollectionRequest.builder().username(name).owned(true).build());
        assertNotNull(response, "CollectionResponse must not be null");
        assertNotEmpty(response.getItems(), "CollectionResponse.items");
    }

    @Test
    void hotEndpointReturnsData() {
        HotResponse response = client.hot().fetchBoardgames();
        assertNotNull(response, "HotResponse must not be null");
        assertNotEmpty(response.getItems(), "HotResponse.items");
    }

    @Test
    void searchEndpointReturnsData() {
        String query = env("BGG_SEARCH_QUERY", "Catan");
        SearchResponse response = client.search().search(query);
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
}
