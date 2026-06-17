package de.agiehl.bgg.model.thing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.agiehl.bgg.model.common.StringValue;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Marketplace listing entry returned when {@code marketplace=1} is requested
 * on a Thing.
 */
@Value
@Builder
@Jacksonized
public class MarketplaceListing {

    /** Date when the listing was created. */
    StringValue listdate;

    /** Asking price. */
    Price price;

    /** Condition of the item, e.g. {@code new}, {@code likenew}. */
    StringValue condition;

    /** Seller notes. */
    StringValue notes;

    /** Link to the listing on BGG. */
    MarketplaceLink link;

    /** Money amount used inside a marketplace listing. */
    @Value
    @Builder
    @Jacksonized
    public static class Price {

        /** ISO currency code, e.g. {@code USD}, {@code EUR}. */
        @JacksonXmlProperty(isAttribute = true)
        String currency;

        /** Asking price as a decimal number. */
        @JacksonXmlProperty(isAttribute = true)
        Double value;
    }

    /** Hyperlink to the listing. */
    @Value
    @Builder
    @Jacksonized
    public static class MarketplaceLink {

        /** Absolute URL of the listing. */
        @JacksonXmlProperty(isAttribute = true)
        String href;

        /** Listing title. */
        @JacksonXmlProperty(isAttribute = true)
        String title;
    }
}
