package de.agiehl.bgg.model.thing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * One ranking entry inside {@link ThingRatings}.
 *
 * <p>A Thing is typically ranked once in the overall {@code Board Game Rank}
 * plus once per applicable family rank (e.g. {@code Strategy Game Rank}).
 */
@Value
@Builder
@Jacksonized
public class ThingRank {

    /** Rank category, e.g. {@code subtype} or {@code family}. */
    @JacksonXmlProperty(isAttribute = true)
    String type;

    /** Internal rank id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Machine readable rank name, e.g. {@code boardgame}, {@code strategygames}. */
    @JacksonXmlProperty(isAttribute = true)
    String name;

    /** Human readable rank name, e.g. {@code "Board Game Rank"}. */
    @JacksonXmlProperty(isAttribute = true)
    String friendlyname;

    /**
     * Rank value as a string, since BGG returns {@code "Not Ranked"} when the
     * item has no rank yet. Use {@link #asIntRank()} for a numeric view.
     */
    @JacksonXmlProperty(isAttribute = true)
    String value;

    /** Bayesian average that this rank is derived from. */
    @JacksonXmlProperty(isAttribute = true)
    Double bayesaverage;

    /**
     * Numeric view of {@link #value}, or {@code null} when the Thing is not
     * ranked yet.
     *
     * @return the parsed rank or {@code null}
     */
    public Integer asIntRank() {
        if (value == null || value.isBlank() || "Not Ranked".equalsIgnoreCase(value.trim())) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
