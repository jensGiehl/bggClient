package de.agiehl.bgg.model.common;

/**
 * The {@code type} parameter accepted by the {@code /forumlist} endpoint.
 */
public enum ForumListType {

    /** Forum list for a Thing. */
    THING("thing"),
    /** Forum list for a Family. */
    FAMILY("family");

    private final String apiValue;

    ForumListType(String apiValue) {
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
