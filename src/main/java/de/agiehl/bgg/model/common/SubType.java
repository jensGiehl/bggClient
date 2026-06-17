package de.agiehl.bgg.model.common;

/**
 * Sub-type filter accepted by the {@code /collection} and {@code /plays}
 * endpoints. Narrower than {@link ThingType}: only the items that can appear
 * in a user's collection are listed here.
 */
public enum SubType {

    /** Board games. */
    BOARDGAME("boardgame"),
    /** Board game expansions. */
    BOARDGAME_EXPANSION("boardgameexpansion"),
    /** Board game accessories. */
    BOARDGAME_ACCESSORY("boardgameaccessory"),
    /** Role playing game items. */
    RPG_ITEM("rpgitem"),
    /** Video games. */
    VIDEOGAME("videogame");

    private final String apiValue;

    SubType(String apiValue) {
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
