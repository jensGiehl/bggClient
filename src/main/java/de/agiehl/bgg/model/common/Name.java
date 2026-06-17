package de.agiehl.bgg.model.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * The {@code <name>} element used by Thing, Family and other endpoints.
 *
 * <p>Example XML: {@code <name type="primary" sortindex="1" value="Catan"/>}.
 */
@Value
@Builder
@Jacksonized
public class Name {

    /**
     * Type of the name. Common values are {@code primary} (the canonical name)
     * and {@code alternate} (a translation or alternate title).
     */
    @JacksonXmlProperty(isAttribute = true)
    String type;

    /** Sort index hint provided by BGG. */
    @JacksonXmlProperty(isAttribute = true)
    Integer sortindex;

    /** The actual name. */
    @JacksonXmlProperty(isAttribute = true)
    String value;
}
