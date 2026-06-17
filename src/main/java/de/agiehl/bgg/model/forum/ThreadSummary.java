package de.agiehl.bgg.model.forum;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Summary entry for one thread inside a forum.
 */
@Value
@Builder
@Jacksonized
public class ThreadSummary {

    /** Thread id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Thread subject. */
    @JacksonXmlProperty(isAttribute = true)
    String subject;

    /** Author username. */
    @JacksonXmlProperty(isAttribute = true)
    String author;

    /** Total number of articles/replies (excluding the original post). */
    @JacksonXmlProperty(isAttribute = true)
    Integer numarticles;

    /** ISO timestamp of the first post. */
    @JacksonXmlProperty(isAttribute = true)
    String postdate;

    /** ISO timestamp of the most recent post. */
    @JacksonXmlProperty(isAttribute = true)
    String lastpostdate;
}
