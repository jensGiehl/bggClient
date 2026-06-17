package de.agiehl.bgg.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.net.URI;
import java.time.Duration;

/**
 * Immutable configuration for a {@link de.agiehl.bgg.BggClient}.
 *
 * <p>Use the generated {@link BggClientConfigBuilder} to construct instances.
 * All settings except {@code apiKey} are optional and ship with sensible defaults
 * suitable for talking to the public BoardGameGeek XML API v2.
 *
 * <p>Example:
 * <pre>{@code
 * BggClientConfig config = BggClientConfig.builder()
 *     .apiKey("my-api-key")
 *     .connectTimeout(Duration.ofSeconds(15))
 *     .build();
 * }</pre>
 */
@Getter
@Builder(toBuilder = true)
public class BggClientConfig {

    /** Default base URI of the BoardGameGeek XML API v2. */
    public static final URI DEFAULT_BASE_URI = URI.create("https://boardgamegeek.com/xmlapi2");

    /** Default user agent reported to the BGG server. */
    public static final String DEFAULT_USER_AGENT = "bgg-client/1.0";

    /** Default name of the query parameter holding the API key. */
    public static final String DEFAULT_API_KEY_PARAMETER = "apikey";

    /** Default connect timeout. */
    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(10);

    /** Default request timeout. */
    public static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(30);

    /** Default number of retries on retryable responses (rate limit, async {@code 202}). */
    public static final int DEFAULT_MAX_RETRIES = 3;

    /** Default backoff between retries. */
    public static final Duration DEFAULT_RETRY_BACKOFF = Duration.ofSeconds(20);

    /**
     * The API key used to authenticate against the BGG XML API. Required.
     *
     * <p>The key is appended to every outgoing request as a query parameter.
     * The parameter name can be customised via {@link #apiKeyParameter}.
     */
    @NonNull
    private final String apiKey;

    /** Base URI to use for all API calls. Defaults to {@link #DEFAULT_BASE_URI}. */
    @NonNull
    @Builder.Default
    private final URI baseUri = DEFAULT_BASE_URI;

    /** Name of the query parameter used to send {@link #apiKey}. */
    @NonNull
    @Builder.Default
    private final String apiKeyParameter = DEFAULT_API_KEY_PARAMETER;

    /** User-Agent header sent with every request. */
    @NonNull
    @Builder.Default
    private final String userAgent = DEFAULT_USER_AGENT;

    /** TCP connect timeout. */
    @NonNull
    @Builder.Default
    private final Duration connectTimeout = DEFAULT_CONNECT_TIMEOUT;

    /** Per-request response timeout. */
    @NonNull
    @Builder.Default
    private final Duration requestTimeout = DEFAULT_REQUEST_TIMEOUT;

    /**
     * Maximum number of retry attempts on retryable responses.
     *
     * <p>Retries are triggered by:
     * <ul>
     *     <li>{@code 202 Accepted} — emitted by the {@code /collection} endpoint
     *         while the response is still being built on the server,</li>
     *     <li>{@code 429 Too Many Requests} — BGG rate-limit response,</li>
     *     <li>{@code 503 Service Unavailable} — transient server overload.</li>
     * </ul>
     *
     * <p>Set to {@code 0} to disable retries entirely. Defaults to
     * {@link #DEFAULT_MAX_RETRIES}.
     */
    @Builder.Default
    private final int maxRetries = DEFAULT_MAX_RETRIES;

    /**
     * Wait time between two retry attempts. Defaults to
     * {@link #DEFAULT_RETRY_BACKOFF}.
     */
    @NonNull
    @Builder.Default
    private final Duration retryBackoff = DEFAULT_RETRY_BACKOFF;

    /**
     * Convenience factory creating a configuration with only an API key set
     * and all other values at their defaults.
     *
     * @param apiKey the BGG API key, must not be {@code null}
     * @return a new immutable configuration
     */
    public static BggClientConfig withApiKey(String apiKey) {
        return BggClientConfig.builder().apiKey(apiKey).build();
    }
}
