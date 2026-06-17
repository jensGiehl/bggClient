package de.agiehl.bgg.request;

import de.agiehl.bgg.http.QueryParameters;
import de.agiehl.bgg.model.common.ThingType;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.Set;

/**
 * Immutable request for the {@code /search} endpoint.
 */
@Value
@Builder
public class SearchRequest {

    /** Search query (required). */
    @NonNull
    String query;

    /** Optional Thing type filter. */
    @Singular
    Set<ThingType> types;

    /** Require an exact name match ({@code exact=1}). */
    boolean exact;

    /**
     * @return the encoded query parameters
     */
    public QueryParameters toQueryParameters() {
        return QueryParameters.create()
                .add("query", query)
                .addCsv("type", types == null ? List.of() : types.stream().map(ThingType::getApiValue).toList())
                .addFlag("exact", exact);
    }
}
