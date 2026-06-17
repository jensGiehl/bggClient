package de.agiehl.bgg.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Internal factory for the Jackson {@link XmlMapper} used to deserialize BGG
 * responses.
 *
 * <p>The mapper is configured leniently: unknown XML elements/attributes are
 * ignored so that future additions to the BGG schema do not break clients that
 * were compiled against an older version of this library.
 */
public final class XmlMapperFactory {

    private XmlMapperFactory() {
    }

    /**
     * Creates a new mapper instance. Each {@link de.agiehl.bgg.BggClient}
     * holds exactly one instance which is thread-safe and may be reused.
     *
     * @return a fully configured {@link XmlMapper}
     */
    public static XmlMapper create() {
        XmlMapper mapper = new XmlMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return mapper;
    }
}
