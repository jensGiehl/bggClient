package de.agiehl.bgg.request;

import de.agiehl.bgg.http.QueryParameters;
import de.agiehl.bgg.model.common.SubType;
import de.agiehl.bgg.model.common.WishlistPriority;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;

/**
 * Immutable request for the {@code /collection} endpoint.
 *
 * <p>This is the most parameter-rich endpoint of the API. All boolean filters
 * are tri-state: leave them unset to skip the filter, set them to {@code true}
 * to include only matching items, or set them to {@code false} to exclude
 * matching items (mapped to {@code 0} by the BGG API).
 */
@Value
@Builder
public class CollectionRequest {

    /** Username (required). */
    @NonNull
    String username;

    /** Sub-type filter. */
    SubType subtype;

    /** Sub-type to exclude. */
    SubType excludeSubtype;

    /** Restrict result to these Thing ids. */
    @Singular
    List<Integer> ids;

    /** Include version data ({@code version=1}). */
    boolean version;

    /** Return a brief representation only ({@code brief=1}). */
    boolean brief;

    /** Include statistics ({@code stats=1}). */
    boolean stats;

    /** Only items the user owns (or only items they do not own when {@code false}). */
    Boolean owned;

    /** Only items the user rated. */
    Boolean rated;

    /** Only items the user played. */
    Boolean played;

    /** Only items the user commented on. */
    Boolean commented;

    /** Only items the user offers for trade. */
    Boolean forTrade;

    /** Only items the user wants. */
    Boolean want;

    /** Only items on the user's wishlist. */
    Boolean wishlist;

    /** Filter wishlist by priority. */
    WishlistPriority wishlistPriority;

    /** Only items the user pre-ordered. */
    Boolean preordered;

    /** Only items the user wants to play. */
    Boolean wantToPlay;

    /** Only items the user wants to buy. */
    Boolean wantToBuy;

    /** Only items the user previously owned. */
    Boolean previouslyOwned;

    /** Only items where the user marked "has parts". */
    Boolean hasParts;

    /** Only items where the user marked "want parts". */
    Boolean wantParts;

    /** Minimum personal rating. */
    Double minRating;

    /** Maximum personal rating. */
    Double rating;

    /** Minimum BGG community rating. */
    Double minBggRating;

    /** Maximum BGG community rating. */
    Double bggRating;

    /** Minimum recorded plays. */
    Integer minPlays;

    /** Maximum recorded plays. */
    Integer maxPlays;

    /** Include private collection metadata ({@code showprivate=1}). */
    boolean showPrivate;

    /** Filter to a single collection entry by collection id. */
    Integer collId;

    /** Only items modified since this ISO timestamp. */
    String modifiedSince;

    /**
     * @return the encoded query parameters
     */
    public QueryParameters toQueryParameters() {
        QueryParameters params = QueryParameters.create()
                .add("username", username)
                .add("subtype", subtype == null ? null : subtype.getApiValue())
                .add("excludesubtype", excludeSubtype == null ? null : excludeSubtype.getApiValue())
                .addCsv("id", ids)
                .addFlag("version", version)
                .addFlag("brief", brief)
                .addFlag("stats", stats)
                .add("wishlistpriority", wishlistPriority == null ? null : wishlistPriority.getApiValue())
                .add("minrating", minRating)
                .add("rating", rating)
                .add("minbggrating", minBggRating)
                .add("bggrating", bggRating)
                .add("minplays", minPlays)
                .add("maxplays", maxPlays)
                .addFlag("showprivate", showPrivate)
                .add("collid", collId)
                .add("modifiedsince", modifiedSince);

        addTernary(params, "own", owned);
        addTernary(params, "rated", rated);
        addTernary(params, "played", played);
        addTernary(params, "comment", commented);
        addTernary(params, "trade", forTrade);
        addTernary(params, "want", want);
        addTernary(params, "wishlist", wishlist);
        addTernary(params, "preordered", preordered);
        addTernary(params, "wanttoplay", wantToPlay);
        addTernary(params, "wanttobuy", wantToBuy);
        addTernary(params, "prevowned", previouslyOwned);
        addTernary(params, "hasparts", hasParts);
        addTernary(params, "wantparts", wantParts);

        return params;
    }

    private static void addTernary(QueryParameters params, String name, Boolean value) {
        if (value != null) {
            params.add(name, value ? "1" : "0");
        }
    }
}
