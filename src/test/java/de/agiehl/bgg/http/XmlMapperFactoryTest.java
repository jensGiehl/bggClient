package de.agiehl.bgg.http;

import de.agiehl.bgg.model.collection.CollectionResponse;
import de.agiehl.bgg.model.thing.ThingRank;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class XmlMapperFactoryTest {

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
