package de.agiehl.bgg.model.collection;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.agiehl.bgg.model.common.NumericBooleanDeserializer;
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
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    Boolean own;

    /** {@code true} when the user previously owned this item. */
    @JacksonXmlProperty(isAttribute = true)
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    Boolean prevowned;

    /** {@code true} when the user offers the item for trade. */
    @JacksonXmlProperty(isAttribute = true)
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    Boolean fortrade;

    /** {@code true} when the user wants this item. */
    @JacksonXmlProperty(isAttribute = true)
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    Boolean want;

    /** {@code true} when the user wants to play this item. */
    @JacksonXmlProperty(isAttribute = true)
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    Boolean wanttoplay;

    /** {@code true} when the user wants to buy this item. */
    @JacksonXmlProperty(isAttribute = true)
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    Boolean wanttobuy;

    /** {@code true} when the item is on the user's wishlist. */
    @JacksonXmlProperty(isAttribute = true)
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    Boolean wishlist;

    /** Wishlist priority (1=must have, 5=don't buy). */
    @JacksonXmlProperty(isAttribute = true)
    Integer wishlistpriority;

    /** {@code true} when the user pre-ordered this item. */
    @JacksonXmlProperty(isAttribute = true)
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    Boolean preordered;

    /** ISO timestamp of the last modification to the user's status. */
    @JacksonXmlProperty(isAttribute = true)
    String lastmodified;
}
