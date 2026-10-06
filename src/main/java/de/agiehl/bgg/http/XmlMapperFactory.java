package de.agiehl.bgg.http;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.dataformat.xml.XmlMapper;

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
        return XmlMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .addHandler(new InvalidNumberAsNullHandler())
                .build();
    }

    /**
     * BGG occasionally uses labels such as {@code "Not Ranked"} in attributes
     * that otherwise contain numbers. Treat those values as missing instead of
     * failing the complete response.
     */
    private static final class InvalidNumberAsNullHandler extends DeserializationProblemHandler {

        @Override
        public Object handleWeirdStringValue(DeserializationContext context, Class<?> targetType,
                                             String value, String failureMessage) {
            if (Number.class.isAssignableFrom(targetType)) {
                return null;
            }
            return NOT_HANDLED;
        }
    }
}
