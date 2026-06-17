package de.agiehl.bgg.model.forum;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Top-level wrapper of the {@code /thread} response.
 */
@Value
@Builder
@Jacksonized
@JacksonXmlRootElement(localName = "thread")
public class ThreadResponse {

    /** Terms of use URL provided by BGG. */
    @JacksonXmlProperty(isAttribute = true)
    String termsofuse;

    /** Thread id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Total number of articles. */
    @JacksonXmlProperty(isAttribute = true)
    Integer numarticles;

    /** Thread link. */
    @JacksonXmlProperty(isAttribute = true)
    String link;

    /** Subject of the thread. */
    String subject;

    /** Articles in this thread, ordered chronologically. */
    @Singular
    @JacksonXmlElementWrapper(localName = "articles")
    @JacksonXmlProperty(localName = "article")
    List<Article> articles;
}
