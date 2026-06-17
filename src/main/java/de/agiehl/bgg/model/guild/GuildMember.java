package de.agiehl.bgg.model.guild;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * One member listed in {@link GuildMembers}.
 */
@Value
@Builder
@Jacksonized
public class GuildMember {

    /** Member username. */
    @JacksonXmlProperty(isAttribute = true)
    String name;

    /** Date the user joined the guild (ISO timestamp). */
    @JacksonXmlProperty(isAttribute = true)
    String date;
}
