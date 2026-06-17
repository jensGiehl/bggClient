package de.agiehl.bgg.request;

import de.agiehl.bgg.http.QueryParameters;
import de.agiehl.bgg.model.common.HotType;
import lombok.Builder;
import lombok.Value;

/**
 * Immutable request for the {@code /hot} endpoint.
 */
@Value
@Builder
public class HotRequest {

    /** Type filter (defaults to {@code boardgame} on BGG's side when omitted). */
    HotType type;

    /**
     * @return the encoded query parameters
     */
    public QueryParameters toQueryParameters() {
        return QueryParameters.create()
                .add("type", type == null ? null : type.getApiValue());
    }
}
