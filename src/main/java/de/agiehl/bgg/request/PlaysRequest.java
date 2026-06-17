package de.agiehl.bgg.request;

import de.agiehl.bgg.http.QueryParameters;
import de.agiehl.bgg.model.common.PlaysType;
import de.agiehl.bgg.model.common.SubType;
import lombok.Builder;
import lombok.Value;

/**
 * Immutable request for the {@code /plays} endpoint.
 *
 * <p>Exactly one of {@link #username} or {@link #id} must be set.
 */
@Value
@Builder(toBuilder = true)
public class PlaysRequest {

    /** BGG username — mutually exclusive with {@link #id}. */
    String username;

    /** Id of the Thing/Family to fetch plays for. */
    Integer id;

    /** Type associated with {@link #id}. */
    PlaysType type;

    /** Minimum play date ({@code YYYY-MM-DD}). */
    String minDate;

    /** Maximum play date ({@code YYYY-MM-DD}). */
    String maxDate;

    /** Filter plays by sub-type. */
    SubType subtype;

    /** Page (1-based) — each page returns up to 100 plays. */
    Integer page;

    /**
     * @return the encoded query parameters
     */
    public QueryParameters toQueryParameters() {
        if (username == null && id == null) {
            throw new IllegalStateException("PlaysRequest requires either username or id");
        }
        if (username != null && id != null) {
            throw new IllegalStateException("PlaysRequest cannot have both username and id");
        }
        return QueryParameters.create()
                .add("username", username)
                .add("id", id)
                .add("type", type == null ? null : type.getApiValue())
                .add("mindate", minDate)
                .add("maxdate", maxDate)
                .add("subtype", subtype == null ? null : subtype.getApiValue())
                .add("page", page);
    }
}
