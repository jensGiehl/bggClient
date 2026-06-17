package de.agiehl.bgg.model.user;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * One guild a user is a member of.
 */
@Value
@Builder
@Jacksonized
public class UserGuild {

    /** Guild id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Guild name. */
    @JacksonXmlProperty(isAttribute = true)
    String name;
}
