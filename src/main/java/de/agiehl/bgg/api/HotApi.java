package de.agiehl.bgg.api;

import de.agiehl.bgg.http.HttpExecutor;
import de.agiehl.bgg.model.common.HotType;
import de.agiehl.bgg.model.hot.HotResponse;
import de.agiehl.bgg.request.HotRequest;

/**
 * Client for the {@code /hot} endpoint.
 */
public class HotApi {

    static final String PATH = "/hot";

    private final HttpExecutor executor;

    /**
     * @param executor the HTTP executor used to perform the requests
     */
    public HotApi(HttpExecutor executor) {
        this.executor = executor;
    }

    /**
     * Executes the given request.
     *
     * @param request the request
     * @return the parsed response
     */
    public HotResponse fetch(HotRequest request) {
        return executor.get(PATH, request.toQueryParameters(), HotResponse.class);
    }

    /**
     * Convenience shortcut for the current board game hot list.
     *
     * @return the parsed response
     */
    public HotResponse fetchBoardgames() {
        return fetch(HotRequest.builder().type(HotType.BOARDGAME).build());
    }
}
