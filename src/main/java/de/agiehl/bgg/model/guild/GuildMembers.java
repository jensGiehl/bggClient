package de.agiehl.bgg.model.guild;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Paginated wrapper of guild members. Only present when {@code members=1} is
 * requested.
 */
@Value
@Builder
@Jacksonized
public class GuildMembers {

    /** Total number of guild members. */
    @JacksonXmlProperty(isAttribute = true)
    Integer count;

    /** Current page (1-based). */
    @JacksonXmlProperty(isAttribute = true)
    Integer page;

    /** Members on the current page. */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "member")
    List<GuildMember> members;
}
