package connector.services;

import connector.transformer.Transformer;
import connector.transformer.transformers.SteamTransformer;
import models.service.notes.CommonModel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class DataTransformerTest {

    @Test
    void testTransformWithNullBody() {
        DataTransformer transformer = new DataTransformer();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transformer.transform(null, "steam")
        );

        assertEquals("response body or API name can not be null", exception.getMessage());
    }

    @Test
    void testTransformWithNullName() {
        DataTransformer transformer = new DataTransformer();

        byte[] body = "{}".getBytes(StandardCharsets.UTF_8);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> transformer.transform(body, null)
        );

        assertEquals("response body or API name can not be null", exception.getMessage());
    }

    @Test
    void testGetTransformerForNameSteam() {
        DataTransformer dataTransformer = new DataTransformer();

        Transformer<?> transformer = dataTransformer.getTransformerForName("steam");

        assertAll(
                () -> assertNotNull(transformer),
                () -> assertTrue(transformer instanceof SteamTransformer),
                () -> assertEquals("steam", transformer.getName())
        );
    }

    @Test
    void testGetTransformerForNameIgnoreCase() {
        DataTransformer dataTransformer = new DataTransformer();

        Transformer<?> transformer = dataTransformer.getTransformerForName("STEAM");

        assertAll(
                () -> assertNotNull(transformer),
                () -> assertEquals("steam", transformer.getName())
        );
    }

    @Test
    void testGetTransformerForNameBadName() {
        DataTransformer dataTransformer = new DataTransformer();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dataTransformer.getTransformerForName("bad-api")
        );

        assertEquals("no transformer found for source: bad-api", exception.getMessage());
    }

    @Test
    void testTransformSteamGood() {
        DataTransformer dataTransformer = new DataTransformer();

        byte[] body = goodSteamJson().getBytes(StandardCharsets.UTF_8);

        CommonModel<?> result = dataTransformer.transform(body, "steam");

        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(result.getUUID()),
                () -> assertNotNull(result.getTimeStamp()),
                () -> assertEquals("steam", result.getSource()),
                () -> assertNotNull(result.getData()),
                () -> assertEquals(1, result.getData().size())
        );
    }

    @Test
    void testTransformBadApi() {
        DataTransformer dataTransformer = new DataTransformer();

        byte[] body = "{}".getBytes(StandardCharsets.UTF_8);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> dataTransformer.transform(body, "bad-api")
        );

        assertEquals("unknown API or transformer", exception.getMessage());
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
}