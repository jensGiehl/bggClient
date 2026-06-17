package de.agiehl.bgg.model.collection;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.agiehl.bgg.model.common.DecimalValue;
import de.agiehl.bgg.model.common.IntValue;
import de.agiehl.bgg.model.thing.ThingRank;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Rating block inside a {@link CollectionStats} entry. Returned when
 * {@code stats=1} is requested on the collection endpoint.
 */
@Value
@Builder
@Jacksonized
public class CollectionRating {

    /**
     * The rating the user gave, as a string ({@code N/A} when the user did not
     * rate the item). Use {@link #asDouble()} for a numeric view.
     */
    @JacksonXmlProperty(isAttribute = true)
    String value;

    /** Number of users that have rated this Thing. */
    IntValue usersrated;

    /** Arithmetic average rating. */
    DecimalValue average;

    /** Bayesian average. */
    DecimalValue bayesaverage;

    /** Standard deviation. */
    DecimalValue stddev;

    /** Median rating. */
    DecimalValue median;

    /** Individual ranks (board game rank, family ranks, ...). */
    @Singular
    @JacksonXmlElementWrapper(localName = "ranks")
    @JacksonXmlProperty(localName = "rank")
    List<ThingRank> ranks;

    /**
     * Parsed numeric view of {@link #value}, or {@code null} when the user
     * did not assign a rating.
     *
     * @return the parsed rating between 1.0 and 10.0 or {@code null}
     */
    public Double asDouble() {
        if (value == null || value.isBlank() || "N/A".equalsIgnoreCase(value.trim())) {
            return null;
        }
        try {
            return Double.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
