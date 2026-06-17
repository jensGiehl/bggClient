package de.agiehl.bgg.model.forum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.agiehl.bgg.model.common.NumericBooleanDeserializer;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Summary entry for one forum within a {@link ForumListResponse}.
 */
@Value
@Builder
@Jacksonized
public class ForumSummary {

    /** Forum id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Group identifier ({@code 0} for the default group). */
    @JacksonXmlProperty(isAttribute = true)
    Integer groupid;

    /** Forum title. */
    @JacksonXmlProperty(isAttribute = true)
    String title;

    /** {@code true} when the forum does not allow new threads. */
    @JacksonXmlProperty(isAttribute = true)
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    Boolean noposting;

    /** Forum description. */
    @JacksonXmlProperty(isAttribute = true)
    String description;

    /** Total number of threads in this forum. */
    @JacksonXmlProperty(isAttribute = true)
    Integer numthreads;

    /** Total number of posts in this forum. */
    @JacksonXmlProperty(isAttribute = true)
    Integer numposts;

    /** ISO timestamp of the most recent post. */
    @JacksonXmlProperty(isAttribute = true)
    String lastpostdate;
}
