package de.agiehl.bgg.request;

import de.agiehl.bgg.http.QueryParameters;
import de.agiehl.bgg.model.common.GuildSort;
import lombok.Builder;
import lombok.Value;

/**
 * Immutable request for the {@code /guild} endpoint.
 */
@Value
@Builder(toBuilder = true)
public class GuildRequest {

    /** Guild id (required). */
    int id;

    /** Include member list ({@code members=1}). */
    boolean members;

    /** Sort order for members. */
    GuildSort sort;

    /** Page of members to return (1-based). */
    Integer page;

    /**
     * @return the encoded query parameters
     */
    public QueryParameters toQueryParameters() {
        return QueryParameters.create()
                .add("id", id)
                .addFlag("members", members)
                .add("sort", sort == null ? null : sort.getApiValue())
                .add("page", page);
    }
}
