package de.agiehl.bgg.request;

import de.agiehl.bgg.http.QueryParameters;
import de.agiehl.bgg.model.common.Domain;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Immutable request for the {@code /user} endpoint.
 */
@Value
@Builder(toBuilder = true)
public class UserRequest {

    /** Username (required). */
    @NonNull
    String name;

    /** Include buddies ({@code buddies=1}). */
    boolean buddies;

    /** Include guilds ({@code guilds=1}). */
    boolean guilds;

    /** Include the user's personal hot list ({@code hot=1}). */
    boolean hot;

    /** Include the user's personal top-N list ({@code top=1}). */
    boolean top;

    /** Domain filter for hot/top lists. */
    Domain domain;

    /** Page for buddies/guilds lists. */
    Integer page;

    /**
     * @return the encoded query parameters
     */
    public QueryParameters toQueryParameters() {
        return QueryParameters.create()
                .add("name", name)
                .addFlag("buddies", buddies)
                .addFlag("guilds", guilds)
                .addFlag("hot", hot)
                .addFlag("top", top)
                .add("domain", domain == null ? null : domain.getApiValue())
                .add("page", page);
    }
}
