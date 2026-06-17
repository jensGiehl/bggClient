package de.agiehl.bgg.model.user;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * One entry in a user's buddy list.
 */
@Value
@Builder
@Jacksonized
public class Buddy {

    /** Buddy's BGG user id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Buddy's BGG user name. */
    @JacksonXmlProperty(isAttribute = true)
    String name;
}
