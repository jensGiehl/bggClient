package de.agiehl.bgg.request;

import de.agiehl.bgg.http.QueryParameters;
import de.agiehl.bgg.model.common.FamilyType;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.Set;

/**
 * Immutable request for the {@code /family} endpoint.
 */
@Value
@Builder
public class FamilyRequest {

    /** Family ids to fetch. At least one id is required. */
    @Singular
    List<Integer> ids;

    /** Optional type filter. */
    @Singular
    Set<FamilyType> types;

    /**
     * Renders the request to query parameters.
     *
     * @return the encoded query parameters
     */
    public QueryParameters toQueryParameters() {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalStateException("FamilyRequest requires at least one id");
        }
        return QueryParameters.create()
                .addCsv("id", ids)
                .addCsv("type", types == null ? List.of() : types.stream().map(FamilyType::getApiValue).toList());
    }
}
