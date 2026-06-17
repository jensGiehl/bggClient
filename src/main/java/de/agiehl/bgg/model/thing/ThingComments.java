package de.agiehl.bgg.model.thing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Paginated wrapper of comments/ratings on a Thing.
 */
@Value
@Builder
@Jacksonized
public class ThingComments {

    /** Current page number (1-based). */
    @JacksonXmlProperty(isAttribute = true)
    Integer page;

    /** Total number of comments across all pages. */
    @JacksonXmlProperty(isAttribute = true)
    Integer totalitems;

    /** Comments on the current page. */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "comment")
    List<ThingComment> comments;
}
