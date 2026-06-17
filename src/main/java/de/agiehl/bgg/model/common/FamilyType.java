package de.agiehl.bgg.model.common;

/**
 * The {@code type} discriminator accepted by the {@code /family} endpoint.
 */
public enum FamilyType {

    /** Role playing game family. */
    RPG("rpg"),
    /** Role playing game periodical family. */
    RPG_PERIODICAL("rpgperiodical"),
    /** Board game family. */
    BOARDGAME_FAMILY("boardgamefamily");

    private final String apiValue;

    FamilyType(String apiValue) {
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
