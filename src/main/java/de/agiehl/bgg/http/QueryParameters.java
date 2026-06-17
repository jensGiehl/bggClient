package de.agiehl.bgg.http;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builder for URL query parameter strings.
 *
 * <p>Designed for the BGG XML API, which uses simple {@code key=value} pairs
 * and accepts comma-separated lists for multi-valued parameters such as
 * {@code id=1,2,3}.
 *
 * <p>Instances are not thread-safe.
 */
public final class QueryParameters {

    private final Map<String, String> parameters = new LinkedHashMap<>();

    private QueryParameters() {
    }

    /**
     * Creates a new empty container.
     *
     * @return a fresh, mutable instance
     */
    public static QueryParameters create() {
        return new QueryParameters();
    }

    /**
     * Adds a parameter. {@code null} values are silently ignored, which allows
     * callers to chain optional parameters without explicit null checks.
     *
     * @param name  the parameter name
     * @param value the parameter value, may be {@code null}
     * @return this instance, for chaining
     */
    public QueryParameters add(String name, Object value) {
        if (value != null) {
            parameters.put(name, value.toString());
        }
        return this;
    }

    /**
     * Adds a boolean parameter using the BGG convention of {@code 1} for
     * {@code true}. When the value is {@code false} or {@code null} the
     * parameter is omitted.
     *
     * @param name  the parameter name
     * @param value the boolean value
     * @return this instance, for chaining
     */
    public QueryParameters addFlag(String name, Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            parameters.put(name, "1");
        }
        return this;
    }

    /**
     * Adds a comma-separated collection. A {@code null} or empty collection is
     * ignored.
     *
     * @param name   the parameter name
     * @param values the values to join
     * @return this instance, for chaining
     */
    public QueryParameters addCsv(String name, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            String joined = values.stream()
                    .filter(java.util.Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
            if (!joined.isEmpty()) {
                parameters.put(name, joined);
            }
        }
        return this;
    }

    /**
     * Renders the parameters into a URL query string, including the leading
     * {@code ?}. Returns an empty string when no parameters have been added.
     *
     * @return the encoded query string
     */
    public String toQueryString() {
        if (parameters.isEmpty()) {
            return "";
        }
        List<String> pairs = new ArrayList<>(parameters.size());
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            pairs.add(encode(entry.getKey()) + "=" + encode(entry.getValue()));
        }
        return "?" + String.join("&", pairs);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
