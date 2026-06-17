package de.agiehl.bgg.model.common;

/**
 * Member ordering accepted by the {@code /guild} endpoint.
 */
public enum GuildSort {

    /** Order by username, ascending. */
    USERNAME("username"),
    /** Order by member join date. */
    DATE("date");

    private final String apiValue;

    GuildSort(String apiValue) {
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
