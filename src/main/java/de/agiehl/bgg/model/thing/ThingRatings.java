package de.agiehl.bgg.model.thing;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.agiehl.bgg.model.common.DecimalValue;
import de.agiehl.bgg.model.common.IntValue;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Aggregated ratings block returned for a Thing when {@code stats=1}.
 */
@Value
@Builder
@Jacksonized
public class ThingRatings {

    /** Number of users that submitted a rating. */
    IntValue usersrated;

    /** Arithmetic average of all ratings. */
    DecimalValue average;

    /** Bayesian average — the value used to compute ranks. */
    DecimalValue bayesaverage;

    /** Standard deviation of all ratings. */
    DecimalValue stddev;

    /** Median rating. */
    DecimalValue median;

    /** Number of users that own a copy. */
    IntValue owned;

    /** Number of users offering this Thing for trade. */
    IntValue trading;

    /** Number of users wanting this Thing. */
    IntValue wanting;

    /** Number of users wishing for this Thing. */
    IntValue wishing;

    /** Total comment count. */
    IntValue numcomments;

    /** Number of weight ratings cast. */
    IntValue numweights;

    /** Average weight rating, between 1.0 (light) and 5.0 (heavy). */
    DecimalValue averageweight;

    /** Individual ranks (board game rank, family ranks, ...). */
    @Singular
    @JacksonXmlElementWrapper(localName = "ranks")
    @JacksonXmlProperty(localName = "rank")
    List<ThingRank> ranks;
}
