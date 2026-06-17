package de.agiehl.bgg.model.thing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * A community video attached to a Thing. Returned when the {@code videos}
 * parameter is enabled.
 */
@Value
@Builder
@Jacksonized
public class ThingVideo {

    /** Video identifier on BGG. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Video title. */
    @JacksonXmlProperty(isAttribute = true)
    String title;

    /** Video category, e.g. {@code review}, {@code session}, {@code instructional}. */
    @JacksonXmlProperty(isAttribute = true)
    String category;

    /** Language tag, e.g. {@code English}. */
    @JacksonXmlProperty(isAttribute = true)
    String language;

    /** Direct link to the video. */
    @JacksonXmlProperty(isAttribute = true)
    String link;

    /** Uploading user's BGG username. */
    @JacksonXmlProperty(isAttribute = true)
    String username;

    /** Uploading user's BGG id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer userid;

    /** ISO timestamp of the post. */
    @JacksonXmlProperty(isAttribute = true)
    String postdate;
}
