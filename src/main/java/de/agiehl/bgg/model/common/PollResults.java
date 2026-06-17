package de.agiehl.bgg.model.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * A {@code <results>} group inside a {@link Poll}.
 *
 * <p>For the {@code suggested_numplayers} poll BGG groups the results by
 * player count, exposed via {@link #getNumplayers()}.
 */
@Value
@Builder
@Jacksonized
public class PollResults {

    /**
     * Optional player count grouping. Only set for the
     * {@code suggested_numplayers} poll.
     */
    @JacksonXmlProperty(isAttribute = true)
    String numplayers;

    /** Individual result buckets. */
    @Singular("result")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "result")
    List<PollResult> results;
}
