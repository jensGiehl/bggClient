package de.agiehl.bgg.model.collection;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Collection status flags returned for each {@link CollectionItem}.
 */
@Value
@Builder
@Jacksonized
public class CollectionStatus {

    /** {@code true} when the user owns this item. */
    @JacksonXmlProperty(isAttribute = true)
    Boolean own;

    /** {@code true} when the user previously owned this item. */
    @JacksonXmlProperty(isAttribute = true)
    Boolean prevowned;

    /** {@code true} when the user offers the item for trade. */
    @JacksonXmlProperty(isAttribute = true)
    Boolean fortrade;

    /** {@code true} when the user wants this item. */
    @JacksonXmlProperty(isAttribute = true)
    Boolean want;

    /** {@code true} when the user wants to play this item. */
    @JacksonXmlProperty(isAttribute = true)
    Boolean wanttoplay;

    /** {@code true} when the user wants to buy this item. */
    @JacksonXmlProperty(isAttribute = true)
    Boolean wanttobuy;

    /** {@code true} when the item is on the user's wishlist. */
    @JacksonXmlProperty(isAttribute = true)
    Boolean wishlist;

    /** Wishlist priority (1=must have, 5=don't buy). */
    @JacksonXmlProperty(isAttribute = true)
    Integer wishlistpriority;

    /** {@code true} when the user pre-ordered this item. */
    @JacksonXmlProperty(isAttribute = true)
    Boolean preordered;

    /** ISO timestamp of the last modification to the user's status. */
    @JacksonXmlProperty(isAttribute = true)
    String lastmodified;
}
