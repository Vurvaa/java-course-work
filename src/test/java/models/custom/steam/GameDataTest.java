package models.custom.steam;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameDataTest {

    @Test
    void testToCsvMapsWithPrice() {
        Pries price = mock(Pries.class);

        when(price.currency()).thenReturn("USD");
        when(price.initial()).thenReturn(1999);
        when(price.discount_percent()).thenReturn(50);
        when(price.final_formatted()).thenReturn("$9.99");

        GameData gameData = new GameData(
                "game",
                "Test Game",
                12345,
                "Some description",
                price
        );

        List<Map<String, String>> result = gameData.toCsvMaps();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("12345", result.get(0).get("steam_appid")),
                () -> assertEquals("Test Game", result.get(0).get("steam_name")),
                () -> assertEquals("game", result.get(0).get("steam_type")),
                () -> assertEquals("Some description", result.get(0).get("steam_description")),
                () -> assertEquals("USD", result.get(0).get("steam_currency")),
                () -> assertEquals("1999", result.get(0).get("steam_initial_price")),
                () -> assertEquals("50%", result.get(0).get("steam_discount")),
                () -> assertEquals("$9.99", result.get(0).get("steam_final_formatted"))
        );
    }

    @Test
    void testToCsvMapsWithoutPrice() {
        GameData gameData = new GameData(
                "game",
                "Free Game",
                777,
                "Free description",
                null
        );

        List<Map<String, String>> result = gameData.toCsvMaps();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("777", result.get(0).get("steam_appid")),
                () -> assertEquals("Free Game", result.get(0).get("steam_name")),
                () -> assertEquals("game", result.get(0).get("steam_type")),
                () -> assertEquals("Free description", result.get(0).get("steam_description")),
                () -> assertEquals("", result.get(0).get("steam_currency")),
                () -> assertEquals("", result.get(0).get("steam_initial_price")),
                () -> assertEquals("", result.get(0).get("steam_discount")),
                () -> assertEquals("", result.get(0).get("steam_final_formatted"))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 50, 100})
    void testDifferentDiscounts(int discount) {
        Pries price = mock(Pries.class);

        when(price.currency()).thenReturn("EUR");
        when(price.initial()).thenReturn(1000);
        when(price.discount_percent()).thenReturn(discount);
        when(price.final_formatted()).thenReturn("10€");

        GameData gameData = new GameData(
                "game",
                "Game",
                1,
                "desc",
                price
        );

        List<Map<String, String>> result = gameData.toCsvMaps();

        assertEquals(discount + "%", result.get(0).get("steam_discount"));
    }

    @Test
    void testRecordFields() {
        Pries price = mock(Pries.class);

        GameData gameData = new GameData(
                "demo",
                "Demo Game",
                555,
                "Demo description",
                price
        );

        assertAll(
                () -> assertEquals("demo", gameData.type()),
                () -> assertEquals("Demo Game", gameData.name()),
                () -> assertEquals(555, gameData.steam_appid()),
                () -> assertEquals("Demo description", gameData.short_description()),
                () -> assertEquals(price, gameData.price_overview())
        );
    }
}