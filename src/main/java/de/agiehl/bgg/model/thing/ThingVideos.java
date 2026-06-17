package de.agiehl.bgg.model.thing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Wrapper of the {@code <videos>} element on a Thing.
 */
@Value
@Builder
@Jacksonized
public class ThingVideos {

    /** Total number of videos available across all pages. */
    @JacksonXmlProperty(isAttribute = true)
    Integer total;

    /** The actual video entries on the current page. */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "video")
    List<ThingVideo> videos;
}
