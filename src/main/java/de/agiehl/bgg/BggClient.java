package de.agiehl.bgg;

import de.agiehl.bgg.api.CollectionApi;
import de.agiehl.bgg.api.FamilyApi;
import de.agiehl.bgg.api.ForumApi;
import de.agiehl.bgg.api.ForumListApi;
import de.agiehl.bgg.api.GuildApi;
import de.agiehl.bgg.api.HotApi;
import de.agiehl.bgg.api.PlaysApi;
import de.agiehl.bgg.api.SearchApi;
import de.agiehl.bgg.api.ThingApi;
import de.agiehl.bgg.api.ThreadApi;
import de.agiehl.bgg.api.UserApi;
import de.agiehl.bgg.config.BggClientConfig;
import de.agiehl.bgg.http.HttpExecutor;
import lombok.Getter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main entry point of the BoardGameGeek XML API v2 client.
 *
 * <p>The client is the only object that consumers of this library need to
 * instantiate. It exposes one accessor per BGG XML endpoint:
 *
 * <ul>
 *     <li>{@link #things()} - {@code /thing}</li>
 *     <li>{@link #families()} - {@code /family}</li>
 *     <li>{@link #forumLists()} - {@code /forumlist}</li>
 *     <li>{@link #forums()} - {@code /forum}</li>
 *     <li>{@link #threads()} - {@code /thread}</li>
 *     <li>{@link #users()} - {@code /user}</li>
 *     <li>{@link #guilds()} - {@code /guild}</li>
 *     <li>{@link #plays()} - {@code /plays}</li>
 *     <li>{@link #collections()} - {@code /collection}</li>
 *     <li>{@link #hot()} - {@code /hot}</li>
 *     <li>{@link #search()} - {@code /search}</li>
 * </ul>
 *
 * <p>The client is fully immutable and thread-safe. A single instance can be
 * shared across an entire application.
 *
 * <p>Typical usage:
 *
 * <pre>{@code
 * BggClient client = BggClient.of("my-api-key");
 *
 * ThingResponse catan = client.things().fetchById(13);
 * SearchResponse hits = client.search().search("Catan");
 * }</pre>
 *
 * <p>For full configuration:
 *
 * <pre>{@code
 * BggClient client = BggClient.builder()
 *     .config(BggClientConfig.builder()
 *         .apiKey("my-api-key")
 *         .requestTimeout(Duration.ofSeconds(60))
 *         .build())
 *     .build();
 * }</pre>
 */
public class BggClient {

    private static final Logger LOGGER = Logger.getLogger(BggClient.class.getName());

    @Getter
    private final BggClientConfig config;

    private final ThingApi things;
    private final FamilyApi families;
    private final ForumListApi forumLists;
    private final ForumApi forums;
    private final ThreadApi threads;
    private final UserApi users;
    private final GuildApi guilds;
    private final PlaysApi plays;
    private final CollectionApi collections;
    private final HotApi hot;
    private final SearchApi search;

    /**
     * Internal constructor used by {@link Builder}. Most callers should use
     * {@link #builder()} or {@link #of(String)}.
     *
     * @param config the immutable configuration
     */
    BggClient(BggClientConfig config) {
        this(config, new HttpExecutor(config));
    }

    /**
     * Constructor that accepts a pre-built {@link HttpExecutor}. Visible for
     * tests and advanced customisation.
     *
     * @param config   the immutable configuration
     * @param executor the HTTP executor to use
     */
    public BggClient(BggClientConfig config, HttpExecutor executor) {
        this.config = config;
        if (LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.log(Level.CONFIG,
                    "BggClient initialised for {0} (maxRetries={1}, retryBackoff={2})",
                    new Object[] {config.getBaseUri(), config.getMaxRetries(), config.getRetryBackoff()});
        }
        this.things = new ThingApi(executor);
        this.families = new FamilyApi(executor);
        this.forumLists = new ForumListApi(executor);
        this.forums = new ForumApi(executor);
        this.threads = new ThreadApi(executor);
        this.users = new UserApi(executor);
        this.guilds = new GuildApi(executor);
        this.plays = new PlaysApi(executor);
        this.collections = new CollectionApi(executor);
        this.hot = new HotApi(executor);
        this.search = new SearchApi(executor);
    }

    /**
     * Convenience factory that creates a client with default configuration
     * and the given API key.
     *
     * @param apiKey the BGG API key, must not be {@code null}
     * @return a fully configured client
     */
    public static BggClient of(String apiKey) {
        return new BggClient(BggClientConfig.withApiKey(apiKey));
    }

    /**
     * @return a new client builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /** @return the {@code /thing} endpoint */
    public ThingApi things() {
        return things;
    }

    /** @return the {@code /family} endpoint */
    public FamilyApi families() {
        return families;
    }

    /** @return the {@code /forumlist} endpoint */
    public ForumListApi forumLists() {
        return forumLists;
    }

    /** @return the {@code /forum} endpoint */
    public ForumApi forums() {
        return forums;
    }

    /** @return the {@code /thread} endpoint */
    public ThreadApi threads() {
        return threads;
    }

    /** @return the {@code /user} endpoint */
    public UserApi users() {
        return users;
    }

    /** @return the {@code /guild} endpoint */
    public GuildApi guilds() {
        return guilds;
    }

    /** @return the {@code /plays} endpoint */
    public PlaysApi plays() {
        return plays;
    }

    /** @return the {@code /collection} endpoint */
    public CollectionApi collections() {
        return collections;
    }

    /** @return the {@code /hot} endpoint */
    public HotApi hot() {
        return hot;
    }

    /** @return the {@code /search} endpoint */
    public SearchApi search() {
        return search;
    }

    /**
     * Fluent builder for {@link BggClient}.
     */
    public static class Builder {

        private BggClientConfig config;

        /**
         * Sets the configuration to use.
         *
         * @param config the configuration, must not be {@code null}
         * @return this builder
         */
        public Builder config(BggClientConfig config) {
            this.config = config;
            return this;
        }

        /**
         * Shortcut equivalent to {@code config(BggClientConfig.withApiKey(apiKey))}.
         *
         * @param apiKey the BGG API key
         * @return this builder
         */
        public Builder apiKey(String apiKey) {
            this.config = BggClientConfig.withApiKey(apiKey);
            return this;
        }

        /**
         * Builds the immutable client.
         *
         * @return a new client instance
         */
        public BggClient build() {
            if (config == null) {
                throw new IllegalStateException("BggClient requires a configuration (set config or apiKey)");
            }
            return new BggClient(config);
        }
    }
}
