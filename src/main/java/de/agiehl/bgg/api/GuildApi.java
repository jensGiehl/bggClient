package de.agiehl.bgg.api;

import de.agiehl.bgg.http.HttpExecutor;
import de.agiehl.bgg.http.PageStream;
import de.agiehl.bgg.model.guild.GuildMember;
import de.agiehl.bgg.model.guild.GuildResponse;
import de.agiehl.bgg.request.GuildRequest;

import java.util.List;
import java.util.stream.Stream;

/**
 * Client for the {@code /guild} endpoint.
 *
 * <p>The API offers three flavours of access:
 *
 * <ul>
 *     <li><b>Single page</b> via {@link #fetch(GuildRequest)}.</li>
 *     <li><b>Lazy stream</b> via {@link #pages(GuildRequest)} or
 *         {@link #members(GuildRequest)}.</li>
 *     <li><b>Eager load-all</b> via {@link #allPages(GuildRequest)} or
 *         {@link #allMembers(GuildRequest)} — every page is fetched up-front
 *         and the result returned as an unmodifiable {@link List}.</li>
 * </ul>
 *
 * <p>All member-paginated variants require the caller to set
 * {@link GuildRequest#isMembers()} to {@code true} on the request — otherwise
 * BGG returns guild metadata without the {@code <members>} block.
 *
 * <p>Stream and eager modes are sequential. Each page fetch goes through the
 * configured retry loop, so rate-limit responses (429/503) are absorbed
 * transparently.
 */
public class GuildApi {

    static final String PATH = "/guild";

    private final HttpExecutor executor;

    /**
     * @param executor the HTTP executor used to perform the requests
     */
    public GuildApi(HttpExecutor executor) {
        this.executor = executor;
    }

    /**
     * Executes the given request.
     *
     * @param request the request
     * @return the parsed response
     */
    public GuildResponse fetch(GuildRequest request) {
        return executor.get(PATH, request.toQueryParameters(), GuildResponse.class);
    }

    /**
     * Convenience shortcut for fetching a guild by id.
     *
     * @param id the guild id
     * @return the parsed response
     */
    public GuildResponse fetchById(int id) {
        return fetch(GuildRequest.builder().id(id).build());
    }

    /**
     * Lazy stream of guild pages. An explicit {@link GuildRequest#getPage()}
     * is honoured as the starting page; otherwise pagination starts at page
     * {@code 1}.
     *
     * @param request the request — must have {@code members(true)} set,
     *                otherwise the stream terminates after the first fetch
     * @return a sequential, lazy stream of pages
     */
    public Stream<GuildResponse> pages(GuildRequest request) {
        int firstPage = request.getPage() == null ? 1 : request.getPage();
        return PageStream.ofPages(
                firstPage,
                page -> fetch(request.toBuilder().page(page).build()),
                response -> response.getMembers() != null
                        && response.getMembers().getMembers() != null
                        && !response.getMembers().getMembers().isEmpty());
    }

    /**
     * Lazy stream flattened to individual {@link GuildMember} entries.
     *
     * @param request the request — must have {@code members(true)} set
     * @return a sequential, lazy stream of guild members
     */
    public Stream<GuildMember> members(GuildRequest request) {
        return pages(request).flatMap(response ->
                response.getMembers() == null || response.getMembers().getMembers() == null
                        ? Stream.empty()
                        : response.getMembers().getMembers().stream());
    }

    /**
     * Eagerly fetches every guild page and returns them in order. Blocks
     * until pagination completes — prefer {@link #pages(GuildRequest)} for
     * guilds with very many members.
     *
     * @param request the request — must have {@code members(true)} set
     * @return an unmodifiable list of every page in order
     */
    public List<GuildResponse> allPages(GuildRequest request) {
        return pages(request).toList();
    }

    /**
     * Eagerly fetches every page and returns all {@link GuildMember} entries
     * in order. Blocks until pagination completes.
     *
     * @param request the request — must have {@code members(true)} set
     * @return an unmodifiable list of every member in order
     */
    public List<GuildMember> allMembers(GuildRequest request) {
        return members(request).toList();
    }
}
