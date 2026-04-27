package connector.transformer.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.custom.games.Game;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GamesTransformerTest {

    @Test
    void testGetName() {
        GamesTransformer transformer = new GamesTransformer();

        String name = transformer.getName();

        assertEquals("top_games", name);
    }

    @Test
    void testGetHeadersCSV() {
        GamesTransformer transformer = new GamesTransformer();

        List<String> headers = transformer.getHeadersCSV();

        assertEquals(
                List.of(
                        "top_games_slug",
                        "top_games_name",
                        "top_games_released",
                        "top_games_rating",
                        "top_games_genre_name"
                ),
                headers
        );
    }

    @Test
    void testHandleResponseBodyGood() {
        GamesTransformer transformer = new GamesTransformer();
        ObjectMapper mapper = new ObjectMapper();

        String json = getGoodGamesJson();

        byte[] body = json.getBytes();

        List<Game> result = transformer.handleResponseBody(body, mapper);

        assertAll(
                () -> assertEquals(2, result.size()),

                () -> assertEquals("gta-v", result.get(0).slug()),
                () -> assertEquals("Grand Theft Auto V", result.get(0).name()),
                () -> assertEquals("2013-09-17", result.get(0).released()),
                () -> assertEquals(4.5, result.get(0).rating()),
                () -> assertEquals("Action", result.get(0).genres().get(0).name()),

                () -> assertEquals("witcher-3", result.get(1).slug()),
                () -> assertEquals("The Witcher 3", result.get(1).name()),
                () -> assertEquals("2015-05-18", result.get(1).released()),
                () -> assertEquals(4.8, result.get(1).rating()),
                () -> assertEquals("RPG", result.get(1).genres().get(0).name())
        );
    }

    @Test
    void testHandleResponseBodyBadJson() {
        GamesTransformer transformer = new GamesTransformer();
        ObjectMapper mapper = new ObjectMapper();

        byte[] body = "bad json".getBytes();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> transformer.handleResponseBody(body, mapper)
        );

        assertAll(
                () -> assertTrue(exception.getMessage().contains("error with reading steam response")),
                () -> assertNotNull(exception.getCause())
        );
    }

    @Test
    void testHandleResponseBodyMapperError() {
        GamesTransformer transformer = new GamesTransformer();

        ObjectMapper mapper = new ObjectMapper();

        byte[] body = "".getBytes();

        assertThrows(
                NullPointerException.class,
                () -> transformer.handleResponseBody(body, mapper)
        );
    }

    private String getGoodGamesJson() {
        return """
                {
                  "results": [
                    {
                      "slug": "gta-v",
                      "name": "Grand Theft Auto V",
                      "released": "2013-09-17",
                      "rating": 4.5,
                      "genres": [
                        {
                          "name": "Action"
                        }
                      ]
                    },
                    {
                      "slug": "witcher-3",
                      "name": "The Witcher 3",
                      "released": "2015-05-18",
                      "rating": 4.8,
                      "genres": [
                        {
                          "name": "RPG"
                        }
                      ]
                    }
                  ]
                }
                """;
    }
}