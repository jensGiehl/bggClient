package de.agiehl.bgg.http;

import de.agiehl.bgg.model.collection.CollectionResponse;
import de.agiehl.bgg.model.collection.CollectionStatus;
import de.agiehl.bgg.model.thing.ThingRank;
import de.agiehl.bgg.model.thing.ThingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlMapperFactoryTest {

    @ParameterizedTest
    @CsvSource({"1, true", "0, false", "true, true", "false, false"})
    void mapsNumericAndTextBooleanAttributes(String value, boolean expected) {
        String xml = "<status own=\"" + value + "\"/>";

        CollectionStatus status = XmlMapperFactory.create().readValue(xml, CollectionStatus.class);

        assertEquals(expected, status.getOwn());
    }

    @Test
    void mapsCollectionTextAndAttributesFromUtf8Bytes() {
        String xml = """
                <items totalitems="1" future="ignored">
                  <item objecttype="thing" objectid="13" subtype="boardgame" collid="1">
                    <name sortindex="1">Fürsten &amp; Händler</name>
                    <status own="1" prevowned="0" wishlist="true"/>
                    <future><value>ignored</value></future>
                  </item>
                </items>
                """;

        CollectionResponse response = XmlMapperFactory.create()
                .readValue(xml.getBytes(StandardCharsets.UTF_8), CollectionResponse.class);

        var item = response.getItems().getFirst();
        assertEquals(1, response.getItems().size());
        assertEquals(13, item.getObjectid());
        assertEquals("Fürsten & Händler", item.getName().getValue());
        assertEquals(1, item.getName().getSortindex());
        assertTrue(item.getStatus().getOwn());
        assertEquals(false, item.getStatus().getPrevowned());
        assertTrue(item.getStatus().getWishlist());
    }

    @Test
    void mapsUnwrappedAndWrappedThingLists() {
        String xml = """
                <items>
                  <item type="boardgame" id="13">
                    <name type="primary" value="Catan"/>
                    <name type="alternate" value="Die Siedler von Catan"/>
                    <link type="boardgamecategory" id="1001" value="Economic" inbound="0"/>
                    <versions><item type="boardgameversion" id="100"/></versions>
                  </item>
                  <item type="boardgame" id="9209">
                    <name type="primary" value="Ticket to Ride"/>
                  </item>
                </items>
                """;

        ThingResponse response = XmlMapperFactory.create().readValue(xml, ThingResponse.class);

        assertEquals(2, response.getItems().size());
        var thing = response.getItems().getFirst();
        assertEquals(2, thing.getNames().size());
        assertEquals("Catan", thing.getPrimaryName());
        assertEquals(1, thing.getLinks().size());
        assertEquals(false, thing.getLinks().getFirst().getInbound());
        assertEquals(1, thing.getVersions().size());
        assertEquals(100, thing.getVersions().getFirst().getId());
        assertEquals("Ticket to Ride", response.getItems().getLast().getPrimaryName());
    }

    @Test
    void mapsInvalidNumericRankValueToNull() throws Exception {
        String xml = """
                <items totalitems="1">
                  <item objecttype="thing" objectid="1" subtype="boardgame" collid="1">
                    <stats>
                      <rating value="N/A">
                        <ranks>
                          <rank type="subtype" id="1" name="boardgame"
                                friendlyname="Board Game Rank" value="Not Ranked"
                                bayesaverage="Not Ranked"/>
                        </ranks>
                      </rating>
                    </stats>
                  </item>
                </items>
                """;

        CollectionResponse response = XmlMapperFactory.create().readValue(xml, CollectionResponse.class);

        ThingRank rank = response.getItems().getFirst().getStats().getRating().getRanks().getFirst();
        assertNull(rank.getBayesaverage());
        assertNull(rank.asIntRank());
    }

    @Test
    void stillMapsValidNumericRankValue() throws Exception {
        String xml = """
                <rank type="subtype" id="1" name="boardgame" value="42" bayesaverage="7.125"/>
                """;

        ThingRank rank = XmlMapperFactory.create().readValue(xml, ThingRank.class);

        assertEquals(7.125, rank.getBayesaverage());
        assertEquals(42, rank.asIntRank());
    }
}
