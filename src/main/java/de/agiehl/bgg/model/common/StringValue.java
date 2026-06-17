package de.agiehl.bgg.model.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Wrapper for the recurring BGG XML pattern of an element whose only content
 * is a single {@code value} attribute holding a string, for example
 * {@code <description value="..."/>}.
 */
@Value
@Builder
@Jacksonized
public class StringValue {

    /** The textual value carried by the {@code value} attribute. */
    @JacksonXmlProperty(isAttribute = true, localName = "value")
    String value;
}
