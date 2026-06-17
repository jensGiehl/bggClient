package de.agiehl.bgg.model.collection;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * The {@code <name>} element of a {@link CollectionItem}. Differs from
 * {@link de.agiehl.bgg.model.common.Name} in that the actual name lives in
 * the element text rather than a {@code value} attribute.
 */
@Value
@Builder
@Jacksonized
public class CollectionName {

    /** Sort hint. */
    @JacksonXmlProperty(isAttribute = true)
    Integer sortindex;

    /** Name text. */
    @JacksonXmlText
    String value;
}
