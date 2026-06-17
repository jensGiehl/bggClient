package de.agiehl.bgg.model.collection;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * One entry in a user's collection.
 */
@Value
@Builder
@Jacksonized
public class CollectionItem {

    /** Object type, e.g. {@code thing}. */
    @JacksonXmlProperty(isAttribute = true)
    String objecttype;

    /** Object id (the Thing/Family id). */
    @JacksonXmlProperty(isAttribute = true)
    Integer objectid;

    /** Sub-type of the object, e.g. {@code boardgame}. */
    @JacksonXmlProperty(isAttribute = true)
    String subtype;

    /** Collection-internal id of this entry. */
    @JacksonXmlProperty(isAttribute = true)
    Integer collid;

    /** Item name. */
    CollectionName name;

    /** Year published. */
    Integer yearpublished;

    /** Full size image URL. */
    String image;

    /** Thumbnail image URL. */
    String thumbnail;

    /** Statistics block. Populated only when {@code stats=1}. */
    CollectionStats stats;

    /** Collection status flags. */
    CollectionStatus status;

    /** Number of plays the user has logged for this Thing. */
    Integer numplays;

    /** User's general comment on this item. */
    String comment;

    /** User's comment on the wishlist entry. */
    String wishlistcomment;
}
