package de.agiehl.bgg.model.user;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Wrapper for a user's {@code hot} or {@code top} list.
 */
@Value
@Builder
@Jacksonized
public class UserList {

    /** Domain of the list (e.g. {@code boardgame}). */
    @JacksonXmlProperty(isAttribute = true)
    String domain;

    /** Items in the list. */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "item")
    List<UserListItem> items;
}
