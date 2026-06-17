package de.agiehl.bgg.api;

import de.agiehl.bgg.http.HttpExecutor;
import de.agiehl.bgg.model.forum.ThreadResponse;
import de.agiehl.bgg.request.ThreadRequest;

/**
 * Client for the {@code /thread} endpoint, returning the articles of a single
 * thread.
 */
public class ThreadApi {

    static final String PATH = "/thread";

    private final HttpExecutor executor;

    /**
     * @param executor the HTTP executor used to perform the requests
     */
    public ThreadApi(HttpExecutor executor) {
        this.executor = executor;
    }

    /**
     * Executes the given request.
     *
     * @param request the request
     * @return the parsed response
     */
    public ThreadResponse fetch(ThreadRequest request) {
        return executor.get(PATH, request.toQueryParameters(), ThreadResponse.class);
    }

    /**
     * Convenience shortcut for fetching a thread by id with default filters.
     *
     * @param id the thread id
     * @return the parsed response
     */
    public ThreadResponse fetchById(int id) {
        return fetch(ThreadRequest.builder().id(id).build());
    }
}
