package de.agiehl.bgg.api;

import de.agiehl.bgg.http.HttpExecutor;
import de.agiehl.bgg.model.family.FamilyResponse;
import de.agiehl.bgg.request.FamilyRequest;

/**
 * Client for the {@code /family} endpoint.
 */
public class FamilyApi {

    static final String PATH = "/family";

    private final HttpExecutor executor;

    /**
     * @param executor the HTTP executor used to perform the requests
     */
    public FamilyApi(HttpExecutor executor) {
        this.executor = executor;
    }

    /**
     * Executes a family lookup.
     *
     * @param request the request, never {@code null}
     * @return the parsed response
     */
    public FamilyResponse fetch(FamilyRequest request) {
        return executor.get(PATH, request.toQueryParameters(), FamilyResponse.class);
    }

    /**
     * Convenience shortcut for fetching a single family by id.
     *
     * @param id the BGG id of the family
     * @return the parsed response
     */
    public FamilyResponse fetchById(int id) {
        return fetch(FamilyRequest.builder().id(id).build());
    }
}
