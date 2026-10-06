package de.agiehl.bgg.model.common;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

public class NumericBooleanDeserializer extends ValueDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser parser, DeserializationContext context) {
        String value = parser.getString();
        if ("1".equals(value)) {
            return true;
        }
        if ("0".equals(value)) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }
}
