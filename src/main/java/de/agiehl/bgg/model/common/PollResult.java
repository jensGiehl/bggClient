package de.agiehl.bgg.model.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * One row of a {@link Poll} result set.
 */
@Value
@Builder
@Jacksonized
public class PollResult {

    /** Vote bucket label, e.g. {@code Best}, {@code Recommended}, {@code Not Recommended}. */
    @JacksonXmlProperty(isAttribute = true)
    String value;

    /** Number of votes in this bucket. */
    @JacksonXmlProperty(isAttribute = true)
    Integer numvotes;

    /**
     * Numeric level — only present for the {@code language_dependence} poll.
     */
    @JacksonXmlProperty(isAttribute = true)
    Integer level;
}
