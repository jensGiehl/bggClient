package de.agiehl.bgg.model.hot;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.agiehl.bgg.model.common.IntValue;
import de.agiehl.bgg.model.common.StringValue;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * One entry of the BGG hotness list.
 */
@Value
@Builder
@Jacksonized
public class HotItem {

    /** Thing id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Position within the hot list (1 = top). */
    @JacksonXmlProperty(isAttribute = true)
    Integer rank;

    /** Thumbnail image URL. */
    StringValue thumbnail;

    /** Primary name. */
    StringValue name;

    /** Year published. */
    IntValue yearpublished;
}
