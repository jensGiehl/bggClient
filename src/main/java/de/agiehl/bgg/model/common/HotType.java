package de.agiehl.bgg.model.common;

/**
 * The {@code type} parameter accepted by the {@code /hot} endpoint.
 */
public enum HotType {

    /** Board games. */
    BOARDGAME("boardgame"),
    /** Role playing games. */
    RPG("rpg"),
    /** Video games. */
    VIDEOGAME("videogame"),
    /** Board game related people. */
    BOARDGAME_PERSON("boardgameperson"),
    /** Role playing game related people. */
    RPG_PERSON("rpgperson"),
    /** Video game related people. */
    VIDEOGAME_PERSON("videogameperson"),
    /** Board game related companies. */
    BOARDGAME_COMPANY("boardgamecompany"),
    /** Role playing game related companies. */
    RPG_COMPANY("rpgcompany"),
    /** Video game related companies. */
    VIDEOGAME_COMPANY("videogamecompany");

    private final String apiValue;

    HotType(String apiValue) {
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
