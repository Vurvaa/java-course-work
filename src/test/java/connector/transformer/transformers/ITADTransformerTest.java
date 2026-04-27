package connector.transformer.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.custom.itad.GameInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ITADTransformerTest {

    @Test
    void testGetName() {
        ITADTransformer transformer = new ITADTransformer();

        String name = transformer.getName();

        assertEquals("itad", name);
    }

    @Test
    void testGetHeadersCSV() {
        ITADTransformer transformer = new ITADTransformer();

        List<String> headers = transformer.getHeadersCSV();

        assertEquals(
                List.of(
                        "itad_id",
                        "itad_title",
                        "itad_achievements",
                        "itad_tags",
                        "itad_review_source",
                        "itad_review_score",
                        "itad_review_url"
                ),
                headers
        );
    }

    @Test
    void testHandleResponseBodyGood() {
        ITADTransformer transformer = new ITADTransformer();
        ObjectMapper mapper = new ObjectMapper();

        String json = """
                {
                  "id": "game-id",
                  "title": "Test Game",
                  "achievements": true,
                  "tags": ["Action", "RPG"],
                  "reviews": [
                    {
                      "source": "Steam",
                      "score": 90,
                      "url": "https://example.com"
                    }
                  ]
                }
                """;

        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        List<GameInfo> result = transformer.handleResponseBody(body, mapper);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals("game-id", result.get(0).id()),
                () -> assertEquals("Test Game", result.get(0).title())
        );
    }

    @Test
    void testHandleResponseBodyMapperError() throws Exception {
        ITADTransformer transformer = new ITADTransformer();

        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.readValue(any(byte[].class), eq(GameInfo.class)))
                .thenThrow(new IOException("test error"));

        byte[] body = "{}".getBytes(StandardCharsets.UTF_8);

        List<GameInfo> result = transformer.handleResponseBody(body, mapper);

        assertNull(result);
    }
}