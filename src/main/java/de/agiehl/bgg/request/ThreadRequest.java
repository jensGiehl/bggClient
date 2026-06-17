package de.agiehl.bgg.request;

import de.agiehl.bgg.http.QueryParameters;
import lombok.Builder;
import lombok.Value;

/**
 * Immutable request for the {@code /thread} endpoint.
 */
@Value
@Builder
public class ThreadRequest {

    /** Thread id. */
    int id;

    /** Filter: return only articles with an id greater or equal to this. */
    Integer minArticleId;

    /** Filter: return only articles posted on or after this date ({@code YYYY-MM-DD hh:mm:ss}). */
    String minArticleDate;

    /** Limit the number of returned articles. */
    Integer count;

    /**
     * @return the encoded query parameters
     */
    public QueryParameters toQueryParameters() {
        return QueryParameters.create()
                .add("id", id)
                .add("minarticleid", minArticleId)
                .add("minarticledate", minArticleDate)
                .add("count", count);
    }
}
