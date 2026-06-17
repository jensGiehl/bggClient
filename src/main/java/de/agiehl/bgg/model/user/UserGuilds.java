package de.agiehl.bgg.model.user;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Paginated wrapper of a user's guilds.
 */
@Value
@Builder
@Jacksonized
public class UserGuilds {

    /** Total number of guilds. */
    @JacksonXmlProperty(isAttribute = true)
    Integer total;

    /** Current page. */
    @JacksonXmlProperty(isAttribute = true)
    Integer page;

    /** Guilds on the current page. */
    @Singular("guild")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "guild")
    List<UserGuild> guilds;
}
