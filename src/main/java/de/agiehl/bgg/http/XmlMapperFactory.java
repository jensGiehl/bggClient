package de.agiehl.bgg.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

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
        mapper.addHandler(new InvalidNumberAsNullHandler());
        return mapper;
    }

    /**
     * BGG occasionally uses labels such as {@code "Not Ranked"} in attributes
     * that otherwise contain numbers. Treat those values as missing instead of
     * failing the complete response.
     */
    private static final class InvalidNumberAsNullHandler extends DeserializationProblemHandler {

        @Override
        public Object handleWeirdStringValue(DeserializationContext context, Class<?> targetType,
                                             String value, String failureMessage) throws IOException {
            if (Number.class.isAssignableFrom(targetType)) {
                return null;
            }
            return NOT_HANDLED;
        }
    }
}
