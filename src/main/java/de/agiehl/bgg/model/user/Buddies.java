package de.agiehl.bgg.model.user;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Paginated wrapper of a user's buddies.
 */
@Value
@Builder
@Jacksonized
public class Buddies {

    /** Total number of buddies. */
    @JacksonXmlProperty(isAttribute = true)
    Integer total;

    /** Current page (1-based). */
    @JacksonXmlProperty(isAttribute = true)
    Integer page;

    /** Buddies on the current page. */
    @Singular("buddy")
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "buddy")
    List<Buddy> buddies;
}
