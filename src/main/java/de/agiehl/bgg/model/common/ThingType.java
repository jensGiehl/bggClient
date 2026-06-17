package de.agiehl.bgg.model.common;

/**
 * The {@code type} discriminator used by the {@code /thing} endpoint.
 *
 * <p>Each enum constant carries the exact string accepted by the BGG XML API
 * via {@link #getApiValue()}.
 */
public enum ThingType {

    /** A board game. */
    BOARDGAME("boardgame"),
    /** An expansion to a board game. */
    BOARDGAME_EXPANSION("boardgameexpansion"),
    /** An accessory to a board game. */
    BOARDGAME_ACCESSORY("boardgameaccessory"),
    /** A video game. */
    VIDEOGAME("videogame"),
    /** A role playing game book or supplement. */
    RPG_ITEM("rpgitem"),
    /** A magazine issue belonging to a role playing game. */
    RPG_ISSUE("rpgissue");

    private final String apiValue;

    ThingType(String apiValue) {
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
