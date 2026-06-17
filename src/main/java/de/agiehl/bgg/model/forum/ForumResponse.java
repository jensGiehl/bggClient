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
 * Top-level wrapper of the {@code /forum} response.
 */
@Value
@Builder
@Jacksonized
@JacksonXmlRootElement(localName = "forum")
public class ForumResponse {

    /** Terms of use URL provided by BGG. */
    @JacksonXmlProperty(isAttribute = true)
    String termsofuse;

    /** Forum id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Forum title. */
    @JacksonXmlProperty(isAttribute = true)
    String title;

    /** Forum description. */
    @JacksonXmlProperty(isAttribute = true)
    String description;

    /** Total number of threads. */
    @JacksonXmlProperty(isAttribute = true)
    Integer numthreads;

    /** Total number of posts. */
    @JacksonXmlProperty(isAttribute = true)
    Integer numposts;

    /** ISO timestamp of the latest post. */
    @JacksonXmlProperty(isAttribute = true)
    String lastpostdate;

    /** {@code true} when the forum does not allow new threads. */
    @JacksonXmlProperty(isAttribute = true)
    Boolean noposting;

    /** Threads on the requested page. */
    @Singular("thread")
    @JacksonXmlElementWrapper(localName = "threads")
    @JacksonXmlProperty(localName = "thread")
    List<ThreadSummary> threads;
}
