package de.agiehl.bgg.request;

import de.agiehl.bgg.http.QueryParameters;
import lombok.Builder;
import lombok.Value;

/**
 * Immutable request for the {@code /forum} endpoint.
 */
@Value
@Builder(toBuilder = true)
public class ForumRequest {

    /** Id of the forum. */
    int id;

    /** Page of threads to retrieve (1-based). */
    Integer page;

    /**
     * @return the encoded query parameters
     */
    public QueryParameters toQueryParameters() {
        return QueryParameters.create()
                .add("id", id)
                .add("page", page);
    }
}
