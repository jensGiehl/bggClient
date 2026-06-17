package de.agiehl.bgg.model.search;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.agiehl.bgg.model.common.IntValue;
import de.agiehl.bgg.model.common.Name;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * One result of a {@code /search} call.
 */
@Value
@Builder
@Jacksonized
public class SearchItem {

    /** Thing type, e.g. {@code boardgame}, {@code boardgameexpansion}. */
    @JacksonXmlProperty(isAttribute = true)
    String type;

    /** BGG identifier. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Primary or matched name. */
    Name name;

    /** Year published, if known. */
    IntValue yearpublished;
}
