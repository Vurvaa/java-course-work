package models.custom.offers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OfferTest {

    @Test
    void testToCsvMapsWithOneDeal() {
        Info info = new Info("Test Game", "12345", "image.png");
        Price price = new Price("4.99", 1710000000);
        Deal deal = new Deal("1", "5.99", "19.99", "70.00");

        Offer offer = new Offer(info, price, List.of(deal));

        List<Map<String, String>> result = offer.toCsvMaps();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Test Game", result.get(0).get("shop_title"))
        );
    }

    @Test
    void testToCsvMapsWithTwoDeals() {
        Info info = new Info("Test Game", "12345", "image.png");
        Price price = new Price("4.99", 1710000000);

        Deal deal1 = new Deal("1", "5.99", "19.99", "70.00");
        Deal deal2 = new Deal("2", "6.99", "29.99", "80.00");

        Offer offer = new Offer(info, price, List.of(deal1, deal2));

        List<Map<String, String>> result = offer.toCsvMaps();

        assertAll(
                () -> assertEquals(2, result.size()),

                () -> assertEquals("Test Game", result.get(0).get("shop_title")),
                () -> assertEquals("12345", result.get(0).get("shop_steam_id")),

                () -> assertEquals("Test Game", result.get(1).get("shop_title")),
                () -> assertEquals("12345", result.get(1).get("shop_steam_id"))
        );
    }

    @Test
    void testToCsvMapsWithEmptyDeals() {
        Info info = new Info("Test Game", "12345", "image.png");
        Price price = new Price("4.99", 1710000000);

        Offer offer = new Offer(info, price, List.of());

        List<Map<String, String>> result = offer.toCsvMaps();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Test Game", result.get(0).get("shop_title"))
        );
    }

    @Test
    void testToCsvMapsWithNullDeals() {
        Info info = new Info("Test Game", "12345", "image.png");
        Price price = new Price("4.99", 1710000000);

        Offer offer = new Offer(info, price, null);

        List<Map<String, String>> result = offer.toCsvMaps();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Test Game", result.get(0).get("shop_title"))
        );
    }

    @Test
    void testToCsvMapsWithNullInfoAndNullPrice() {
        Deal deal = new Deal("1", "5.99", "19.99", "70.00");

        Offer offer = new Offer(null, null, List.of(deal));

        List<Map<String, String>> result = offer.toCsvMaps();

        assertEquals(1, result.size());
    }

    @Test
    void testRecordFields() {
        Info info = new Info("Test Game", "12345", "image.png");
        Price price = new Price("4.99", 1710000000);
        Deal deal = new Deal("1", "5.99", "19.99", "70.00");

        Offer offer = new Offer(info, price, List.of(deal));

        assertAll(
                () -> assertEquals(info, offer.info()),
                () -> assertEquals(price, offer.cheapestPriceEver()),
                () -> assertEquals(List.of(deal), offer.deals())
        );
    }
}