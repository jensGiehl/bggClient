package de.agiehl.bgg.model.plays;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Top-level wrapper of the {@code /plays} response.
 */
@Value
@Builder
@Jacksonized
@JacksonXmlRootElement(localName = "plays")
public class PlaysResponse {

    /** Terms of use URL provided by BGG. */
    @JacksonXmlProperty(isAttribute = true)
    String termsofuse;

    /** Username plays belong to. */
    @JacksonXmlProperty(isAttribute = true)
    String username;

    /** User id plays belong to. */
    @JacksonXmlProperty(isAttribute = true)
    Integer userid;

    /** Total number of plays across all pages. */
    @JacksonXmlProperty(isAttribute = true)
    Integer total;

    /** Current page (1-based). */
    @JacksonXmlProperty(isAttribute = true)
    Integer page;

    /** Plays on the current page. */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "play")
    List<Play> plays;
}
