package de.agiehl.bgg.model.search;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Top-level wrapper of the {@code /search} response.
 */
@Value
@Builder
@Jacksonized
@JacksonXmlRootElement(localName = "items")
public class SearchResponse {

    /** Terms of use URL provided by BGG. */
    @JacksonXmlProperty(isAttribute = true)
    String termsofuse;

    /** Total number of matches. */
    @JacksonXmlProperty(isAttribute = true)
    Integer total;

    /** The actual matches. */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "item")
    List<SearchItem> items;
}
