package de.agiehl.bgg.http;

import de.agiehl.bgg.model.collection.CollectionPrivateInfo;
import de.agiehl.bgg.model.collection.CollectionResponse;
import de.agiehl.bgg.model.collection.CollectionStatus;
import de.agiehl.bgg.model.thing.ThingRank;
import de.agiehl.bgg.model.thing.ThingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
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
    void mapsPrivateCollectionAttributesAndCommentWithoutRoundingMoney() {
        String xml = """
                <items totalitems="1">
                  <item objecttype="thing" objectid="13" subtype="boardgame" collid="1">
                    <privateinfo pp_currency="EUR" pricepaid="12345678901234567890.1234567890"
                                 cv_currency="USD" currvalue="59.95" quantity="2"
                                 acquisitiondate="2026-10-10" acquiredfrom="Händler &amp; Freunde"
                                 inventorylocation="Büro, Regal 1">
                      <privatecomment>Geschenk für die Familie</privatecomment>
                    </privateinfo>
                  </item>
                </items>
                """;

        CollectionResponse response = XmlMapperFactory.create()
                .readValue(xml.getBytes(StandardCharsets.UTF_8), CollectionResponse.class);

        CollectionPrivateInfo privateInfo = response.getItems().getFirst().getPrivateinfo();
        assertEquals("EUR", privateInfo.getPpCurrency());
        assertEquals(new BigDecimal("12345678901234567890.1234567890"), privateInfo.getPricepaid());
        assertEquals("USD", privateInfo.getCvCurrency());
        assertEquals(new BigDecimal("59.95"), privateInfo.getCurrvalue());
        assertEquals(2, privateInfo.getQuantity());
        assertEquals("2026-10-10", privateInfo.getAcquisitiondate());
        assertEquals("Händler & Freunde", privateInfo.getAcquiredfrom());
        assertEquals("Büro, Regal 1", privateInfo.getInventorylocation());
        assertEquals("Geschenk für die Familie", privateInfo.getPrivatecomment());
    }

    @Test
    void mapsCollectionWithoutPrivateInformation() {
        String xml = """
                <items totalitems="1">
                  <item objecttype="thing" objectid="13" subtype="boardgame" collid="1"/>
                </items>
                """;

        CollectionResponse response = XmlMapperFactory.create().readValue(xml, CollectionResponse.class);

        assertNull(response.getItems().getFirst().getPrivateinfo());
    }

    @Test
    void mapsEmptyPrivateNumericAttributesAndMissingOptionalFields() {
        String xml = """
                <privateinfo pricepaid="" currvalue="" quantity="" acquisitiondate="">
                  <privatecomment/>
                </privateinfo>
                """;

        CollectionPrivateInfo privateInfo = XmlMapperFactory.create().readValue(xml, CollectionPrivateInfo.class);

        assertNull(privateInfo.getPricepaid());
        assertNull(privateInfo.getCurrvalue());
        assertNull(privateInfo.getQuantity());
        assertNull(privateInfo.getPpCurrency());
        assertNull(privateInfo.getCvCurrency());
        assertNull(privateInfo.getAcquiredfrom());
        assertNull(privateInfo.getInventorylocation());
        assertEquals("", privateInfo.getAcquisitiondate());
        assertEquals("", privateInfo.getPrivatecomment());
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
