package de.agiehl.bgg.model.guild;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Physical address attached to a guild.
 */
@Value
@Builder
@Jacksonized
public class GuildLocation {

    /** First address line. */
    String addr1;

    /** Second address line. */
    String addr2;

    /** City. */
    String city;

    /** State or province. */
    String stateorprovince;

    /** Postal code. */
    String postalcode;

    /** Country. */
    String country;
}
