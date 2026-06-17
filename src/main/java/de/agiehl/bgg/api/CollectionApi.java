package de.agiehl.bgg.api;

import de.agiehl.bgg.http.HttpExecutor;
import de.agiehl.bgg.model.collection.CollectionResponse;
import de.agiehl.bgg.request.CollectionRequest;

/**
 * Client for the {@code /collection} endpoint.
 *
 * <p>The collection endpoint may return {@code 202 Accepted} when BGG is
 * still building the response. The {@link HttpExecutor} transparently retries
 * such responses (as well as rate-limit {@code 429} and {@code 503}) using
 * {@link de.agiehl.bgg.config.BggClientConfig#getMaxRetries()} and
 * {@link de.agiehl.bgg.config.BggClientConfig#getRetryBackoff()}.
 */
public class CollectionApi {

    static final String PATH = "/collection";

    private final HttpExecutor executor;

    /**
     * @param executor the HTTP executor used to perform the requests
     */
    public CollectionApi(HttpExecutor executor) {
        this.executor = executor;
    }

    /**
     * Executes the given request.
     *
     * @param request the request
     * @return the parsed response
     */
    public CollectionResponse fetch(CollectionRequest request) {
        return executor.get(PATH, request.toQueryParameters(), CollectionResponse.class);
    }

    /**
     * Convenience shortcut for fetching a user's full collection without any
     * filters.
     *
     * @param username the BGG username
     * @return the parsed response
     */
    public CollectionResponse fetchByUsername(String username) {
        return fetch(CollectionRequest.builder().username(username).build());
    }
}
