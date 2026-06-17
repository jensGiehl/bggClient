package de.agiehl.bgg.request;

import de.agiehl.bgg.http.QueryParameters;
import de.agiehl.bgg.model.common.ThingType;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.Set;

/**
 * Immutable request describing one call to the {@code /thing} endpoint.
 *
 * <p>Construct via the generated builder:
 *
 * <pre>{@code
 * ThingRequest request = ThingRequest.builder()
 *     .id(13)
 *     .stats(true)
 *     .videos(true)
 *     .build();
 * }</pre>
 */
@Value
@Builder(toBuilder = true)
public class ThingRequest {

    /** The {@code id}s of the Things to fetch. At least one id is required. */
    @Singular
    List<Integer> ids;

    /** Optional type filter — only Things of one of these types are returned. */
    @Singular
    Set<ThingType> types;

    /** Include version data ({@code versions=1}). */
    boolean versions;

    /** Include videos ({@code videos=1}). */
    boolean videos;

    /** Include statistics ({@code stats=1}). */
    boolean stats;

    /** Include marketplace listings ({@code marketplace=1}). */
    boolean marketplace;

    /** Include comments ({@code comments=1}). Mutually exclusive with {@link #ratingComments}. */
    boolean comments;

    /** Include rating comments only ({@code ratingcomments=1}). */
    boolean ratingComments;

    /** Page number when fetching paginated comments. */
    Integer page;

    /** Page size when fetching paginated comments. Maximum is 100. */
    Integer pageSize;

    /**
     * Renders this request to a {@link QueryParameters} bag, ready to be sent
     * to {@link de.agiehl.bgg.http.HttpExecutor}.
     *
     * @return the query parameters
     */
    public QueryParameters toQueryParameters() {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalStateException("ThingRequest requires at least one id");
        }
        if (comments && ratingComments) {
            throw new IllegalStateException("comments and ratingComments cannot both be enabled");
        }
        return QueryParameters.create()
                .addCsv("id", ids)
                .addCsv("type", types == null ? List.of() : types.stream().map(ThingType::getApiValue).toList())
                .addFlag("versions", versions)
                .addFlag("videos", videos)
                .addFlag("stats", stats)
                .addFlag("marketplace", marketplace)
                .addFlag("comments", comments)
                .addFlag("ratingcomments", ratingComments)
                .add("page", page)
                .add("pagesize", pageSize);
    }
}
