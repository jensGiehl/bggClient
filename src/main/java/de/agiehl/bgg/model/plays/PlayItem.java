package de.agiehl.bgg.model.plays;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.agiehl.bgg.model.common.StringValue;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Object played in a recorded {@link Play}.
 */
@Value
@Builder
@Jacksonized
public class PlayItem {

    /** Item name. */
    @JacksonXmlProperty(isAttribute = true)
    String name;

    /** Object type, e.g. {@code thing}. */
    @JacksonXmlProperty(isAttribute = true)
    String objecttype;

    /** Object id — refers to a Thing or Family id depending on {@link #objecttype}. */
    @JacksonXmlProperty(isAttribute = true)
    Integer objectid;

    /** Subtypes of the played item, e.g. {@code boardgame}, {@code boardgameexpansion}. */
    @Singular
    @JacksonXmlElementWrapper(localName = "subtypes")
    @JacksonXmlProperty(localName = "subtype")
    List<StringValue> subtypes;
}
