package de.agiehl.bgg.api;

import de.agiehl.bgg.http.HttpExecutor;
import de.agiehl.bgg.http.PageStream;
import de.agiehl.bgg.model.forum.ForumResponse;
import de.agiehl.bgg.model.forum.ThreadSummary;
import de.agiehl.bgg.request.ForumRequest;

import java.util.List;
import java.util.stream.Stream;

/**
 * Client for the {@code /forum} endpoint, returning the threads of a single
 * forum.
 *
 * <p>The API offers three flavours of access:
 *
 * <ul>
 *     <li><b>Single page</b> via {@link #fetch(ForumRequest)}.</li>
 *     <li><b>Lazy stream</b> via {@link #pages(ForumRequest)} or
 *         {@link #threads(ForumRequest)}.</li>
 *     <li><b>Eager load-all</b> via {@link #allPages(ForumRequest)} or
 *         {@link #allThreads(ForumRequest)} — every page is fetched up-front
 *         and the result returned as an unmodifiable {@link List}.</li>
 * </ul>
 *
 * <p>Stream and eager modes are sequential — running them in parallel would
 * multiply rate-limit hits.
 */
public class ForumApi {

    static final String PATH = "/forum";

    private final HttpExecutor executor;

    /**
     * @param executor the HTTP executor used to perform the requests
     */
    public ForumApi(HttpExecutor executor) {
        this.executor = executor;
    }

    /**
     * Executes the given request.
     *
     * @param request the request
     * @return the parsed response
     */
    public ForumResponse fetch(ForumRequest request) {
        return executor.get(PATH, request.toQueryParameters(), ForumResponse.class);
    }

    /**
     * Convenience shortcut for fetching the first page of a forum.
     *
     * @param id the forum id
     * @return the parsed response
     */
    public ForumResponse fetchById(int id) {
        return fetch(ForumRequest.builder().id(id).build());
    }

    /**
     * Lazy stream of forum pages. An explicit {@link ForumRequest#getPage()}
     * on the request is honoured as the starting page; otherwise pagination
     * starts at page {@code 1}.
     *
     * @param request the request (the {@code page} field is overridden per fetch)
     * @return a sequential, lazy stream of pages
     */
    public Stream<ForumResponse> pages(ForumRequest request) {
        int firstPage = request.getPage() == null ? 1 : request.getPage();
        return PageStream.ofPages(
                firstPage,
                page -> fetch(request.toBuilder().page(page).build()),
                response -> response.getThreads() != null && !response.getThreads().isEmpty());
    }

    /**
     * Lazy stream flattened to individual {@link ThreadSummary} entries.
     *
     * @param request the request (the {@code page} field is overridden per fetch)
     * @return a sequential, lazy stream of thread summaries
     */
    public Stream<ThreadSummary> threads(ForumRequest request) {
        return pages(request).flatMap(response ->
                response.getThreads() == null ? Stream.empty() : response.getThreads().stream());
    }

    /**
     * Eagerly fetches every forum page and returns them in order. Blocks until
     * pagination completes — prefer {@link #pages(ForumRequest)} for forums
     * with hundreds of pages.
     *
     * @param request the request (the {@code page} field is overridden per fetch)
     * @return an unmodifiable list of every page in order
     */
    public List<ForumResponse> allPages(ForumRequest request) {
        return pages(request).toList();
    }

    /**
     * Eagerly fetches every page and returns all {@link ThreadSummary}
     * entries in order. Blocks until pagination completes.
     *
     * @param request the request (the {@code page} field is overridden per fetch)
     * @return an unmodifiable list of every thread summary in order
     */
    public List<ThreadSummary> allThreads(ForumRequest request) {
        return threads(request).toList();
    }
}
