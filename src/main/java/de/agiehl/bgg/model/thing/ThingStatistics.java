package de.agiehl.bgg.model.thing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Statistics block on a Thing — present when {@code stats=1} is requested.
 */
@Value
@Builder
@Jacksonized
public class ThingStatistics {

    /** Page number — currently always {@code 1} in the API. */
    @JacksonXmlProperty(isAttribute = true)
    Integer page;

    /** Aggregated ratings. */
    ThingRatings ratings;
}
