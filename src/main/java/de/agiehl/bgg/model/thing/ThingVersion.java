package de.agiehl.bgg.model.thing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.agiehl.bgg.model.common.DecimalValue;
import de.agiehl.bgg.model.common.IntValue;
import de.agiehl.bgg.model.common.Link;
import de.agiehl.bgg.model.common.Name;
import de.agiehl.bgg.model.common.StringValue;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * A specific version of a Thing — different printings, editions, language
 * variants and so on. Returned when {@code versions=1}.
 */
@Value
@Builder
@Jacksonized
public class ThingVersion {

    /** Version type, typically {@code boardgameversion}. */
    @JacksonXmlProperty(isAttribute = true)
    String type;

    /** Version identifier. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Thumbnail image URL. */
    String thumbnail;

    /** Full-size image URL. */
    String image;

    /** All names of this version (primary + alternates). */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "name")
    List<Name> names;

    /** Links to publishers, languages and other related Things. */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    List<Link> links;

    /** Year this version was published. */
    IntValue yearpublished;

    /** Manufacturer's product code. */
    StringValue productcode;

    /** Physical width in inches. */
    DecimalValue width;

    /** Physical length in inches. */
    DecimalValue length;

    /** Physical depth in inches. */
    DecimalValue depth;

    /** Weight in pounds. */
    DecimalValue weight;
}
