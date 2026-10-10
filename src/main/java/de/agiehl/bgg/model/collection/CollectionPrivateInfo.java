package de.agiehl.bgg.model.collection;

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

/**
 * Private details of a collection entry, returned for its authenticated owner
 * when private collection information is requested.
 */
@Value
@Builder
@Jacksonized
public class CollectionPrivateInfo {

    /** Currency of the purchase price. */
    @JacksonXmlProperty(isAttribute = true, localName = "pp_currency")
    String ppCurrency;

    /** Purchase price, preserving the exact decimal amount. */
    @JacksonXmlProperty(isAttribute = true)
    BigDecimal pricepaid;

    /** Currency of the current value. */
    @JacksonXmlProperty(isAttribute = true, localName = "cv_currency")
    String cvCurrency;

    /** Current value, preserving the exact decimal amount. */
    @JacksonXmlProperty(isAttribute = true)
    BigDecimal currvalue;

    /** Number of copies recorded by the owner. */
    @JacksonXmlProperty(isAttribute = true)
    Integer quantity;

    /** Acquisition date as supplied by BGG, usually in {@code yyyy-MM-dd} format. */
    @JacksonXmlProperty(isAttribute = true)
    String acquisitiondate;

    /** Seller or other source from which the item was acquired. */
    @JacksonXmlProperty(isAttribute = true)
    String acquiredfrom;

    /** Owner's inventory location for the item. */
    @JacksonXmlProperty(isAttribute = true)
    String inventorylocation;

    /** Owner's private comment on the item. */
    String privatecomment;
}
