package de.agiehl.bgg.model.guild;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Top-level wrapper of the {@code /guild} response.
 */
@Value
@Builder
@Jacksonized
@JacksonXmlRootElement(localName = "guild")
public class GuildResponse {

    /** Terms of use URL provided by BGG. */
    @JacksonXmlProperty(isAttribute = true)
    String termsofuse;

    /** Guild id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Guild name. */
    @JacksonXmlProperty(isAttribute = true)
    String name;

    /** Creation timestamp. */
    @JacksonXmlProperty(isAttribute = true)
    String created;

    /** Guild category. */
    String category;

    /** Guild website URL. */
    String website;

    /** Guild manager (username). */
    String manager;

    /** Long form guild description. */
    String description;

    /** Address. */
    GuildLocation location;

    /** Member list. Populated only when {@code members=1}. */
    GuildMembers members;
}
