package de.agiehl.bgg.request;

import de.agiehl.bgg.http.QueryParameters;
import de.agiehl.bgg.model.common.ForumListType;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Immutable request for the {@code /forumlist} endpoint.
 */
@Value
@Builder
public class ForumListRequest {

    /** Id of the Thing/Family whose forum list should be retrieved. */
    int id;

    /** Type of the parent — required. */
    @NonNull
    ForumListType type;

    /**
     * @return the encoded query parameters
     */
    public QueryParameters toQueryParameters() {
        return QueryParameters.create()
                .add("id", id)
                .add("type", type.getApiValue());
    }
}
