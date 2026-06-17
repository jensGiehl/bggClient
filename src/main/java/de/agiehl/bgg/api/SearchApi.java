package de.agiehl.bgg.api;

import de.agiehl.bgg.http.HttpExecutor;
import de.agiehl.bgg.model.search.SearchResponse;
import de.agiehl.bgg.request.SearchRequest;

/**
 * Client for the {@code /search} endpoint.
 */
public class SearchApi {

    static final String PATH = "/search";

    private final HttpExecutor executor;

    /**
     * @param executor the HTTP executor used to perform the requests
     */
    public SearchApi(HttpExecutor executor) {
        this.executor = executor;
    }

    /**
     * Executes the given request.
     *
     * @param request the request
     * @return the parsed response
     */
    public SearchResponse fetch(SearchRequest request) {
        return executor.get(PATH, request.toQueryParameters(), SearchResponse.class);
    }

    /**
     * Convenience shortcut for a fuzzy search by query string.
     *
     * @param query the search term
     * @return the parsed response
     */
    public SearchResponse search(String query) {
        return fetch(SearchRequest.builder().query(query).build());
    }
}
