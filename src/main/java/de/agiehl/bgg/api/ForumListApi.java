package de.agiehl.bgg.api;

import de.agiehl.bgg.http.HttpExecutor;
import de.agiehl.bgg.model.forum.ForumListResponse;
import de.agiehl.bgg.request.ForumListRequest;

/**
 * Client for the {@code /forumlist} endpoint, returning the list of forums
 * attached to a Thing or Family.
 */
public class ForumListApi {

    static final String PATH = "/forumlist";

    private final HttpExecutor executor;

    /**
     * @param executor the HTTP executor used to perform the requests
     */
    public ForumListApi(HttpExecutor executor) {
        this.executor = executor;
    }

    /**
     * Executes the given request.
     *
     * @param request the request
     * @return the parsed response
     */
    public ForumListResponse fetch(ForumListRequest request) {
        return executor.get(PATH, request.toQueryParameters(), ForumListResponse.class);
    }
}
