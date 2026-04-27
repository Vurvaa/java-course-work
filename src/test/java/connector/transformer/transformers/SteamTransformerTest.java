package connector.transformer.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.custom.steam.GameData;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SteamTransformerTest {

    @Test
    void testGetName() {
        SteamTransformer transformer = new SteamTransformer();

        String name = transformer.getName();

        assertEquals("steam", name);
    }

    @Test
    void testGetHeadersCSV() {
        SteamTransformer transformer = new SteamTransformer();

        List<String> headers = transformer.getHeadersCSV();

        assertEquals(
                List.of(
                        "steam_appid",
                        "steam_name",
                        "steam_type",
                        "steam_description",
                        "steam_currency",
                        "steam_initial_price",
                        "steam_discount",
                        "steam_final_formatted"
                ),
                headers
        );
    }

    @Test
    void testHandleResponseBodyGood() {
        SteamTransformer transformer = new SteamTransformer();
        ObjectMapper mapper = new ObjectMapper();

        byte[] body = goodSteamJson().getBytes();

        List<GameData> result = transformer.handleResponseBody(body, mapper);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals("game", result.get(0).type()),
                () -> assertEquals("Test Game", result.get(0).name()),
                () -> assertEquals(3764200, result.get(0).steam_appid()),
                () -> assertEquals("Some description", result.get(0).short_description()),
                () -> assertNotNull(result.get(0).price_overview()),
                () -> assertEquals("USD", result.get(0).price_overview().currency()),
                () -> assertEquals(1999, result.get(0).price_overview().initial()),
                () -> assertEquals(50, result.get(0).price_overview().discount_percent()),
                () -> assertEquals("$9.99", result.get(0).price_overview().final_formatted())
        );
    }

    @Test
    void testHandleResponseBodyWithoutPrice() {
        SteamTransformer transformer = new SteamTransformer();
        ObjectMapper mapper = new ObjectMapper();

        byte[] body = steamJsonWithoutPrice().getBytes();

        List<GameData> result = transformer.handleResponseBody(body, mapper);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Free Game", result.get(0).name()),
                () -> assertNull(result.get(0).price_overview())
        );
    }

    @Test
    void testHandleResponseBodyBadJson() {
        SteamTransformer transformer = new SteamTransformer();
        ObjectMapper mapper = new ObjectMapper();

        byte[] body = "bad json".getBytes();

        List<GameData> result = transformer.handleResponseBody(body, mapper);

        assertNull(result);
    }

    private String goodSteamJson() {
        return """
                {
                  "3764200": {
                    "success": true,
                    "data": {
                      "type": "game",
                      "name": "Test Game",
                      "steam_appid": 3764200,
                      "short_description": "Some description",
                      "price_overview": {
                        "currency": "USD",
                        "initial": 1999,
                        "discount_percent": 50,
                        "final_formatted": "$9.99"
                      }
                    }
                  }
                }
                """;
    }

    private String steamJsonWithoutPrice() {
        return """
                {
                  "3764200": {
                    "success": true,
                    "data": {
                      "type": "game",
                      "name": "Free Game",
                      "steam_appid": 3764200,
                      "short_description": "Free description",
                      "price_overview": null
                    }
                  }
                }
                """;
    }
}