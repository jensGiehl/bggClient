package de.agiehl.bgg.model.thing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Single user comment or rating returned by {@code comments=1} or
 * {@code ratingcomments=1} on the Thing endpoint.
 */
@Value
@Builder
@Jacksonized
public class ThingComment {

    /** Commenting user's name. */
    @JacksonXmlProperty(isAttribute = true)
    String username;

    /** Rating given by the user as a string; BGG uses {@code "N/A"} when missing. */
    @JacksonXmlProperty(isAttribute = true)
    String rating;

    /** Comment text. */
    @JacksonXmlProperty(isAttribute = true)
    String value;

    /**
     * Parsed numeric view of {@link #rating}, or {@code null} when the user
     * did not assign a rating.
     *
     * @return the parsed rating between 1.0 and 10.0 or {@code null}
     */
    public Double asDoubleRating() {
        if (rating == null || rating.isBlank() || "N/A".equalsIgnoreCase(rating.trim())) {
            return null;
        }
        try {
            return Double.valueOf(rating.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
