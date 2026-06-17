package de.agiehl.bgg.api;

import de.agiehl.bgg.http.HttpExecutor;
import de.agiehl.bgg.http.PageStream;
import de.agiehl.bgg.model.thing.Thing;
import de.agiehl.bgg.model.thing.ThingComment;
import de.agiehl.bgg.model.thing.ThingComments;
import de.agiehl.bgg.model.thing.ThingResponse;
import de.agiehl.bgg.request.ThingRequest;

import java.util.List;
import java.util.stream.Stream;

/**
 * Client for the {@code /thing} endpoint.
 *
 * <p>The endpoint returns one or more Things — typically board games — with
 * optional add-on data: ratings, statistics, videos, marketplace listings,
 * versions and comments.
 *
 * <p>Usage:
 *
 * <pre>{@code
 * ThingResponse response = bggClient.things().fetch(
 *     ThingRequest.builder()
 *         .id(13)
 *         .stats(true)
 *         .build());
 * }</pre>
 *
 * <p>For paginated comment retrieval the API offers two flavours:
 *
 * <ul>
 *     <li><b>Lazy stream</b> via {@link #pages(ThingRequest)} or
 *         {@link #comments(ThingRequest)}.</li>
 *     <li><b>Eager load-all</b> via {@link #allPages(ThingRequest)} or
 *         {@link #allComments(ThingRequest)} — every comment page is fetched
 *         up-front and the result returned as an unmodifiable {@link List}.</li>
 * </ul>
 *
 * <p>These are most meaningful for single-id requests with
 * {@code comments(true)} or {@code ratingComments(true)} set — multi-id
 * requests work but the page cursor is shared across all returned Things on
 * the BGG side.
 */
public class ThingApi {

    static final String PATH = "/thing";

    private final HttpExecutor executor;

    /**
     * @param executor the HTTP executor used to perform the requests
     */
    public ThingApi(HttpExecutor executor) {
        this.executor = executor;
    }

    /**
     * Executes the given request and returns the parsed response.
     *
     * @param request the request, never {@code null}
     * @return the parsed response
     */
    public ThingResponse fetch(ThingRequest request) {
        return executor.get(PATH, request.toQueryParameters(), ThingResponse.class);
    }

    /**
     * Convenience shortcut for fetching a single Thing by id with default
     * options.
     *
     * @param id the BGG id of the Thing
     * @return the parsed response
     */
    public ThingResponse fetchById(int id) {
        return fetch(ThingRequest.builder().id(id).build());
    }

    /**
     * Lazy stream of comment pages. Use with a single-id request that has
     * {@code comments(true)} or {@code ratingComments(true)} set. An explicit
     * {@link ThingRequest#getPage()} on the request is honoured as the
     * starting page; otherwise pagination starts at page {@code 1}.
     *
     * @param request the request (the {@code page} field is overridden per fetch)
     * @return a sequential, lazy stream of pages
     */
    public Stream<ThingResponse> pages(ThingRequest request) {
        int firstPage = request.getPage() == null ? 1 : request.getPage();
        return PageStream.ofPages(
                firstPage,
                page -> fetch(request.toBuilder().page(page).build()),
                ThingApi::anyCommentsOnPage);
    }

    /**
     * Lazy stream flattened to individual {@link ThingComment} entries across
     * all Things in the response and across pages.
     *
     * @param request the request — should have {@code comments(true)} or
     *                {@code ratingComments(true)} set
     * @return a sequential, lazy stream of comments
     */
    public Stream<ThingComment> comments(ThingRequest request) {
        return pages(request).flatMap(response ->
                response.getItems() == null
                        ? Stream.<ThingComment>empty()
                        : response.getItems().stream().flatMap(ThingApi::commentsOf));
    }

    /**
     * Eagerly fetches every Thing comment page and returns them in order.
     * Blocks until pagination completes.
     *
     * @param request the request (the {@code page} field is overridden per fetch)
     * @return an unmodifiable list of every page in order
     */
    public List<ThingResponse> allPages(ThingRequest request) {
        return pages(request).toList();
    }

    /**
     * Eagerly fetches every page and returns all {@link ThingComment} entries
     * in order. Blocks until pagination completes.
     *
     * @param request the request — should have {@code comments(true)} or
     *                {@code ratingComments(true)} set
     * @return an unmodifiable list of every comment in order
     */
    public List<ThingComment> allComments(ThingRequest request) {
        return comments(request).toList();
    }

    private static boolean anyCommentsOnPage(ThingResponse response) {
        if (response.getItems() == null) {
            return false;
        }
        return response.getItems().stream().anyMatch(thing -> {
            ThingComments c = thing.getComments();
            return c != null && c.getComments() != null && !c.getComments().isEmpty();
        });
    }

    private static Stream<ThingComment> commentsOf(Thing thing) {
        ThingComments c = thing.getComments();
        if (c == null) {
            return Stream.empty();
        }
        List<ThingComment> list = c.getComments();
        return list == null ? Stream.empty() : list.stream();
    }
}
