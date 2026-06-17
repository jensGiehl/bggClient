package de.agiehl.bgg.model.plays;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.agiehl.bgg.model.common.NumericBooleanDeserializer;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Single recorded play session.
 */
@Value
@Builder
@Jacksonized
public class Play {

    /** Play id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Play date ({@code YYYY-MM-DD}). */
    @JacksonXmlProperty(isAttribute = true)
    String date;

    /** Number of times the game was played in this entry. */
    @JacksonXmlProperty(isAttribute = true)
    Integer quantity;

    /** Length in minutes. */
    @JacksonXmlProperty(isAttribute = true)
    Integer length;

    /** {@code true} when the play was incomplete. */
    @JacksonXmlProperty(isAttribute = true)
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    Boolean incomplete;

    /** {@code true} when the play should not count in statistics. */
    @JacksonXmlProperty(isAttribute = true)
    @JsonDeserialize(using = NumericBooleanDeserializer.class)
    Boolean nowinstats;

    /** Location where the play happened. */
    @JacksonXmlProperty(isAttribute = true)
    String location;

    /** The game that was played. */
    PlayItem item;

    /** User comments attached to the play. */
    String comments;

    /** Players who participated. */
    @Singular
    @JacksonXmlElementWrapper(localName = "players")
    @JacksonXmlProperty(localName = "player")
    List<PlayPlayer> players;
}
