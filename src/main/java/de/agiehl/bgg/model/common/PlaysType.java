package de.agiehl.bgg.model.common;

/**
 * The {@code type} parameter accepted by the {@code /plays} endpoint.
 */
public enum PlaysType {

    /** Plays of a Thing. */
    THING("thing"),
    /** Plays of a Family. */
    FAMILY("family");

    private final String apiValue;

    PlaysType(String apiValue) {
        this.apiValue = apiValue;
    }

    /**
     * @return the literal string accepted by the BGG XML API
     */
    public String getApiValue() {
        return apiValue;
    }

    @Override
    public String toString() {
        return apiValue;
    }
}
