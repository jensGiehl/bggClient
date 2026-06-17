package de.agiehl.bgg.model.collection;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Statistics block on a {@link CollectionItem}. Returned when {@code stats=1}
 * is requested.
 */
@Value
@Builder
@Jacksonized
public class CollectionStats {

    /** Minimum supported number of players. */
    @JacksonXmlProperty(isAttribute = true)
    Integer minplayers;

    /** Maximum supported number of players. */
    @JacksonXmlProperty(isAttribute = true)
    Integer maxplayers;

    /** Minimum playing time in minutes. */
    @JacksonXmlProperty(isAttribute = true)
    Integer minplaytime;

    /** Maximum playing time in minutes. */
    @JacksonXmlProperty(isAttribute = true)
    Integer maxplaytime;

    /** Suggested playing time in minutes. */
    @JacksonXmlProperty(isAttribute = true)
    Integer playingtime;

    /** Number of BGG users owning this Thing. */
    @JacksonXmlProperty(isAttribute = true)
    Integer numowned;

    /** Rating block, including ranks. */
    CollectionRating rating;
}
