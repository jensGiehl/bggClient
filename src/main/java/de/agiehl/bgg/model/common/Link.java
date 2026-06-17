package de.agiehl.bgg.model.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Relationship link between a Thing/Family and another item, for example a
 * board game's category, mechanic, designer, artist, publisher or family.
 *
 * <p>Example XML:
 * {@code <link type="boardgamecategory" id="1042" value="Economic"/>}.
 */
@Value
@Builder
@Jacksonized
public class Link {

    /** The link type, e.g. {@code boardgamecategory}, {@code boardgamemechanic}. */
    @JacksonXmlProperty(isAttribute = true)
    String type;

    /** The id of the linked Thing/Family/Person/Company. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Human-readable label of the linked item. */
    @JacksonXmlProperty(isAttribute = true)
    String value;

    /**
     * {@code true} when this link represents the inbound relationship, e.g.
     * an expansion linked from its base game.
     */
    @JacksonXmlProperty(isAttribute = true)
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    Boolean inbound;
}
