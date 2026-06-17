package de.agiehl.bgg.model.user;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Item shown in a user's personal {@code hot} or {@code top} list.
 */
@Value
@Builder
@Jacksonized
public class UserListItem {

    /** Position within the list. */
    @JacksonXmlProperty(isAttribute = true)
    Integer rank;

    /** Item id (Thing id). */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Item name. */
    @JacksonXmlProperty(isAttribute = true)
    String name;

    /** Type, present in some payloads, e.g. {@code thing}. */
    @JacksonXmlProperty(isAttribute = true)
    String type;
}
