package de.agiehl.bgg.api;

import de.agiehl.bgg.http.HttpExecutor;
import de.agiehl.bgg.http.PageStream;
import de.agiehl.bgg.model.user.Buddy;
import de.agiehl.bgg.model.user.UserGuild;
import de.agiehl.bgg.model.user.UserResponse;
import de.agiehl.bgg.request.UserRequest;

import java.util.List;
import java.util.stream.Stream;

/**
 * Client for the {@code /user} endpoint.
 *
 * <p>The API offers three flavours of access over the paginated sub-resources
 * {@code buddies} and {@code guilds}:
 *
 * <ul>
 *     <li><b>Single page</b> via {@link #fetch(UserRequest)}.</li>
 *     <li><b>Lazy stream</b> via {@link #pages(UserRequest)},
 *         {@link #buddies(UserRequest)} or {@link #guilds(UserRequest)}.</li>
 *     <li><b>Eager load-all</b> via {@link #allPages(UserRequest)},
 *         {@link #allBuddies(UserRequest)} or {@link #allGuilds(UserRequest)}
 *         — every page is fetched up-front and the result returned as an
 *         unmodifiable {@link List}.</li>
 * </ul>
 *
 * <p>{@link #buddies(UserRequest)} / {@link #allBuddies(UserRequest)} require
 * {@link UserRequest#isBuddies()} to be {@code true}; the guild variants
 * require {@link UserRequest#isGuilds()}.
 *
 * <p>BGG paginates buddies and guilds together via a single {@code page}
 * parameter — if both flags are enabled the streams iterate over the same
 * fetched pages. To paginate only one of them, set just the corresponding
 * flag on the request.
 */
public class UserApi {

    static final String PATH = "/user";

    private final HttpExecutor executor;

    /**
     * @param executor the HTTP executor used to perform the requests
     */
    public UserApi(HttpExecutor executor) {
        this.executor = executor;
    }

    /**
     * Executes the given request.
     *
     * @param request the request
     * @return the parsed response
     */
    public UserResponse fetch(UserRequest request) {
        return executor.get(PATH, request.toQueryParameters(), UserResponse.class);
    }

    /**
     * Convenience shortcut returning the basic profile of a user.
     *
     * @param username the BGG username
     * @return the parsed response
     */
    public UserResponse fetchByName(String username) {
        return fetch(UserRequest.builder().name(username).build());
    }

    /**
     * Lazy stream of user pages. Termination relies on either the buddies or
     * guilds list being empty, depending on which flag is enabled on the
     * request.
     *
     * @param request the request — must have at least one of
     *                {@code buddies(true)} or {@code guilds(true)} set
     * @return a sequential, lazy stream of pages
     */
    public Stream<UserResponse> pages(UserRequest request) {
        int firstPage = request.getPage() == null ? 1 : request.getPage();
        return PageStream.ofPages(
                firstPage,
                page -> fetch(request.toBuilder().page(page).build()),
                response -> hasBuddyContent(response) || hasGuildContent(response));
    }

    /**
     * Lazy stream flattened to {@link Buddy} entries. Caller must set
     * {@code buddies(true)} on the request.
     *
     * @param request the request
     * @return a sequential, lazy stream of buddies
     */
    public Stream<Buddy> buddies(UserRequest request) {
        return pages(request).flatMap(response ->
                hasBuddyContent(response)
                        ? response.getBuddies().getBuddies().stream()
                        : Stream.empty());
    }

    /**
     * Lazy stream flattened to {@link UserGuild} entries. Caller must set
     * {@code guilds(true)} on the request.
     *
     * @param request the request
     * @return a sequential, lazy stream of guilds
     */
    public Stream<UserGuild> guilds(UserRequest request) {
        return pages(request).flatMap(response ->
                hasGuildContent(response)
                        ? response.getGuilds().getGuilds().stream()
                        : Stream.empty());
    }

    /**
     * Eagerly fetches every user page and returns them in order. Blocks until
     * pagination completes.
     *
     * @param request the request — must have at least one of
     *                {@code buddies(true)} or {@code guilds(true)} set
     * @return an unmodifiable list of every page in order
     */
    public List<UserResponse> allPages(UserRequest request) {
        return pages(request).toList();
    }

    /**
     * Eagerly fetches every page and returns all {@link Buddy} entries in
     * order. Caller must set {@code buddies(true)} on the request.
     *
     * @param request the request
     * @return an unmodifiable list of every buddy in order
     */
    public List<Buddy> allBuddies(UserRequest request) {
        return buddies(request).toList();
    }

    /**
     * Eagerly fetches every page and returns all {@link UserGuild} entries in
     * order. Caller must set {@code guilds(true)} on the request.
     *
     * @param request the request
     * @return an unmodifiable list of every guild in order
     */
    public List<UserGuild> allGuilds(UserRequest request) {
        return guilds(request).toList();
    }

    private static boolean hasBuddyContent(UserResponse response) {
        return response.getBuddies() != null
                && response.getBuddies().getBuddies() != null
                && !response.getBuddies().getBuddies().isEmpty();
    }

    private static boolean hasGuildContent(UserResponse response) {
        return response.getGuilds() != null
                && response.getGuilds().getGuilds() != null
                && !response.getGuilds().getGuilds().isEmpty();
    }
}
