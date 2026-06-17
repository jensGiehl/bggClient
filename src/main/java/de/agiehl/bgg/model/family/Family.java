package de.agiehl.bgg.model.family;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.agiehl.bgg.model.common.Link;
import de.agiehl.bgg.model.common.Name;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * One Family entry returned by the {@code /family} endpoint.
 */
@Value
@Builder
@Jacksonized
public class Family {

    /** Family type, e.g. {@code boardgamefamily}, {@code rpg}. */
    @JacksonXmlProperty(isAttribute = true)
    String type;

    /** BGG identifier. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Thumbnail image URL. */
    String thumbnail;

    /** Full size image URL. */
    String image;

    /** All names (primary + alternates). */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "name")
    List<Name> names;

    /** Family description. */
    String description;

    /** Links to the members of this family. */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    List<Link> links;
}
