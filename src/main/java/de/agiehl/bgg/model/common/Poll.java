package de.agiehl.bgg.model.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * A community poll attached to a Thing. BGG ships three polls per board game:
 * {@code suggested_numplayers}, {@code suggested_playerage} and
 * {@code language_dependence}.
 */
@Value
@Builder
@Jacksonized
public class Poll {

    /** Internal poll identifier, e.g. {@code suggested_numplayers}. */
    @JacksonXmlProperty(isAttribute = true)
    String name;

    /** Human readable title of the poll. */
    @JacksonXmlProperty(isAttribute = true)
    String title;

    /** Total number of votes cast across all buckets/groups. */
    @JacksonXmlProperty(isAttribute = true)
    Integer totalvotes;

    /** Result groups. */
    @Singular("resultsGroup")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "results")
    List<PollResults> results;
}
