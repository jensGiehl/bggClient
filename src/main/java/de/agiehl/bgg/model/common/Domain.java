package de.agiehl.bgg.model.common;

/**
 * Domain filter used by the {@code /user} endpoint to scope the {@code hot} and
 * {@code top} sub-lists.
 */
public enum Domain {

    /** Board games. */
    BOARDGAME("boardgame"),
    /** Role playing games. */
    RPG("rpg"),
    /** Video games. */
    VIDEOGAME("videogame");

    private final String apiValue;

    Domain(String apiValue) {
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
