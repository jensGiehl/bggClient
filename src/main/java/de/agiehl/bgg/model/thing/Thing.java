package de.agiehl.bgg.model.thing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.agiehl.bgg.model.common.IntValue;
import de.agiehl.bgg.model.common.Link;
import de.agiehl.bgg.model.common.Name;
import de.agiehl.bgg.model.common.Poll;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * A single Thing — board game, expansion, accessory, RPG item, video game,
 * etc. — returned by the {@code /thing} endpoint.
 */
@Value
@Builder
@Jacksonized
public class Thing {

    /** Thing type, e.g. {@code boardgame}, {@code boardgameexpansion}. */
    @JacksonXmlProperty(isAttribute = true)
    String type;

    /** BGG identifier. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Thumbnail image URL. */
    String thumbnail;

    /** Full-size image URL. */
    String image;

    /** All names (primary + alternates). */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "name")
    List<Name> names;

    /** Long form description, HTML-decoded by BGG. */
    String description;

    /** Year of first publication. */
    IntValue yearpublished;

    /** Minimum supported number of players. */
    IntValue minplayers;

    /** Maximum supported number of players. */
    IntValue maxplayers;

    /** Suggested playing time in minutes. */
    IntValue playingtime;

    /** Minimum playing time in minutes. */
    IntValue minplaytime;

    /** Maximum playing time in minutes. */
    IntValue maxplaytime;

    /** Recommended minimum age in years. */
    IntValue minage;

    /** Community polls (suggested players, age, language dependence). */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "poll")
    List<Poll> polls;

    /** Outgoing links: categories, mechanics, designers, publishers, ... */
    @Singular
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "link")
    List<Link> links;

    /** Versions of this Thing. Populated only when {@code versions=1}. */
    @Singular
    @JacksonXmlElementWrapper(localName = "versions")
    @JacksonXmlProperty(localName = "item")
    List<ThingVersion> versions;

    /** Videos of this Thing. Populated only when {@code videos=1}. */
    ThingVideos videos;

    /** Statistics block. Populated only when {@code stats=1}. */
    ThingStatistics statistics;

    /** Marketplace listings. Populated only when {@code marketplace=1}. */
    @Singular
    @JacksonXmlElementWrapper(localName = "marketplacelistings")
    @JacksonXmlProperty(localName = "listing")
    List<MarketplaceListing> marketplaceListings;

    /**
     * Comments and/or ratings. Populated when {@code comments=1} or
     * {@code ratingcomments=1} is set.
     */
    ThingComments comments;

    /**
     * Convenience accessor returning the {@code primary} name's value, or
     * {@code null} when no primary name was returned.
     *
     * @return the primary name string
     */
    public String getPrimaryName() {
        if (names == null) {
            return null;
        }
        return names.stream()
                .filter(n -> "primary".equalsIgnoreCase(n.getType()))
                .map(Name::getValue)
                .findFirst()
                .orElse(null);
    }
}
