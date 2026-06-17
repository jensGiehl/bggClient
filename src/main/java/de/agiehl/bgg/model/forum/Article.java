package de.agiehl.bgg.model.forum;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Article (a single post) inside a thread.
 */
@Value
@Builder
@Jacksonized
public class Article {

    /** Article id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Author username. */
    @JacksonXmlProperty(isAttribute = true)
    String username;

    /** Optional link to the article. */
    @JacksonXmlProperty(isAttribute = true)
    String link;

    /** ISO timestamp of the post. */
    @JacksonXmlProperty(isAttribute = true)
    String postdate;

    /** ISO timestamp of the last edit. */
    @JacksonXmlProperty(isAttribute = true)
    String editdate;

    /** Number of edits made to this article. */
    @JacksonXmlProperty(isAttribute = true)
    Integer numedits;

    /** Subject of the article. */
    String subject;

    /** Body of the article in BBCode. */
    String body;
}
