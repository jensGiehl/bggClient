package de.agiehl.bgg.model.collection;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Top-level wrapper of the {@code /collection} response.
 */
@Value
@Builder
@Jacksonized
@JacksonXmlRootElement(localName = "items")
public class CollectionResponse {

    /** Terms of use URL provided by BGG. */
    @JacksonXmlProperty(isAttribute = true)
    String termsofuse;

    /** Total number of items in the collection (after filtering). */
    @JacksonXmlProperty(isAttribute = true)
    Integer totalitems;

    /** ISO timestamp of when the response was published. */
    @JacksonXmlProperty(isAttribute = true)
    String pubdate;

    /** Items in the collection. */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "item")
    List<CollectionItem> items;
}
