package de.agiehl.bgg.model.user;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import de.agiehl.bgg.model.common.IntValue;
import de.agiehl.bgg.model.common.StringValue;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Top-level wrapper of the {@code /user} response.
 */
@Value
@Builder
@Jacksonized
@JacksonXmlRootElement(localName = "user")
public class UserResponse {

    /** Terms of use URL provided by BGG. */
    @JacksonXmlProperty(isAttribute = true)
    String termsofuse;

    /** Internal user id. */
    @JacksonXmlProperty(isAttribute = true)
    Integer id;

    /** Username. */
    @JacksonXmlProperty(isAttribute = true)
    String name;

    /** First name as provided by the user. */
    StringValue firstname;

    /** Last name as provided by the user. */
    StringValue lastname;

    /** URL of the user's avatar; BGG returns {@code N/A} when not set. */
    StringValue avatarlink;

    /** Year the user registered. */
    IntValue yearregistered;

    /** Last login date ({@code YYYY-MM-DD}). */
    StringValue lastlogin;

    /** State or province. */
    StringValue stateorprovince;

    /** Country. */
    StringValue country;

    /** Personal website URL. */
    StringValue webaddress;

    /** Xbox Live gamer tag. */
    StringValue xboxaccount;

    /** Wii friend code. */
    StringValue wiiaccount;

    /** PlayStation Network account name. */
    StringValue psnaccount;

    /** Battle.net account name. */
    StringValue battlenetaccount;

    /** Steam account name. */
    StringValue steamaccount;

    /** Marketplace trade rating. */
    IntValue traderating;

    /** Buddies block — only populated when {@code buddies=1}. */
    Buddies buddies;

    /** Guilds block — only populated when {@code guilds=1}. */
    UserGuilds guilds;

    /** Personal hot list — only populated when {@code hot=1}. */
    UserList hot;

    /** Personal top list — only populated when {@code top=1}. */
    UserList top;
}
