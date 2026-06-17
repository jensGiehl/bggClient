package de.agiehl.bgg.model.plays;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * One player participating in a recorded {@link Play}.
 */
@Value
@Builder
@Jacksonized
public class PlayPlayer {

    /** Player's BGG username, empty when the player is not a BGG user. */
    @JacksonXmlProperty(isAttribute = true)
    String username;

    /** Player's BGG user id, {@code 0} when unknown. */
    @JacksonXmlProperty(isAttribute = true)
    Integer userid;

    /** Player display name. */
    @JacksonXmlProperty(isAttribute = true)
    String name;

    /** Optional start position. */
    @JacksonXmlProperty(isAttribute = true)
    String startposition;

    /** Color, faction or team played by this player. */
    @JacksonXmlProperty(isAttribute = true)
    String color;

    /** Player score for the play. */
    @JacksonXmlProperty(isAttribute = true)
    String score;

    /** {@code true} when this was the player's first play of the game. */
    @JacksonXmlProperty(isAttribute = true, localName = "new")
    Boolean newPlayer;

    /** Optional rating the player gave the game after this play. */
    @JacksonXmlProperty(isAttribute = true)
    String rating;

    /** {@code true} when this player won the game. */
    @JacksonXmlProperty(isAttribute = true)
    Boolean win;
}
