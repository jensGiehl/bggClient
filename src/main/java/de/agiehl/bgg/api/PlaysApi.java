package de.agiehl.bgg.api;

import de.agiehl.bgg.http.HttpExecutor;
import de.agiehl.bgg.http.PageStream;
import de.agiehl.bgg.model.plays.Play;
import de.agiehl.bgg.model.plays.PlaysResponse;
import de.agiehl.bgg.request.PlaysRequest;

import java.util.List;
import java.util.stream.Stream;

/**
 * Client for the {@code /plays} endpoint.
 *
 * <p>The API offers three flavours of access:
 *
 * <ul>
 *     <li><b>Single page</b> via {@link #fetch(PlaysRequest)}.</li>
 *     <li><b>Lazy stream</b> via {@link #pages(PlaysRequest)} or
 *         {@link #items(PlaysRequest)} — pages are pulled on demand and
 *         consumption can be short-circuited (e.g. {@code limit},
 *         {@code findFirst}).</li>
 *     <li><b>Eager load-all</b> via {@link #allPages(PlaysRequest)} or
 *         {@link #allItems(PlaysRequest)} — every page is fetched up-front and
 *         the result is returned as an unmodifiable {@link List}.</li>
 * </ul>
 *
 * <p>Stream and eager modes are sequential. Pagination terminates when BGG
 * returns an empty page. Each underlying page fetch goes through the
 * configured retry loop, so rate-limit responses (429/503) are absorbed
 * transparently.
 */
public class PlaysApi {

    static final String PATH = "/plays";

    private final HttpExecutor executor;

    /**
     * @param executor the HTTP executor used to perform the requests
     */
    public PlaysApi(HttpExecutor executor) {
        this.executor = executor;
    }

    /**
     * Executes the given request.
     *
     * @param request the request
     * @return the parsed response
     */
    public PlaysResponse fetch(PlaysRequest request) {
        return executor.get(PATH, request.toQueryParameters(), PlaysResponse.class);
    }

    /**
     * Convenience shortcut for fetching the first page of a user's plays.
     *
     * @param username the BGG username
     * @return the parsed response
     */
    public PlaysResponse fetchByUsername(String username) {
        return fetch(PlaysRequest.builder().username(username).build());
    }

    /**
     * Lazy stream of pages. An explicit {@link PlaysRequest#getPage()} on the
     * request is honoured as the starting page; otherwise pagination starts at
     * page {@code 1}.
     *
     * @param request the request (the {@code page} field is overridden per fetch)
     * @return a sequential, lazy stream of pages
     */
    public Stream<PlaysResponse> pages(PlaysRequest request) {
        int firstPage = request.getPage() == null ? 1 : request.getPage();
        return PageStream.ofPages(
                firstPage,
                page -> fetch(request.toBuilder().page(page).build()),
                response -> response.getPlays() != null && !response.getPlays().isEmpty());
    }

    /**
     * Lazy stream flattened to individual {@link Play} entries.
     *
     * @param request the request (the {@code page} field is overridden per fetch)
     * @return a sequential, lazy stream of plays
     */
    public Stream<Play> items(PlaysRequest request) {
        return pages(request).flatMap(response ->
                response.getPlays() == null ? Stream.empty() : response.getPlays().stream());
    }

     /**
      * Eagerly fetches every page and returns all {@link Play} entries from all
      * pages in order. Blocks until pagination completes — prefer
      * {@link #items(PlaysRequest)} for very large datasets where
      * short-circuiting is desirable.
      *
      * @param request the request (the {@code page} field is overridden per fetch)
      * @return an unmodifiable list of every play from all pages in order
      */
     public List<Play> allPages(PlaysRequest request) {
         return items(request).toList();
     }

    /**
     * Eagerly fetches every page and returns all {@link Play} entries in
     * order. Blocks until pagination completes — prefer
     * {@link #items(PlaysRequest)} for very large datasets where
     * short-circuiting is desirable.
     *
     * @param request the request (the {@code page} field is overridden per fetch)
     * @return an unmodifiable list of every play in order
     */
    public List<Play> allItems(PlaysRequest request) {
        return items(request).toList();
    }
}
