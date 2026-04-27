package connector.transformer.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.custom.offers.Offer;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OfferTransformerTest {

    @Test
    void testGetName() {
        OfferTransformer transformer = new OfferTransformer();

        String name = transformer.getName();

        assertEquals("shop", name);
    }

    @Test
    void testGetHeadersCSV() {
        OfferTransformer transformer = new OfferTransformer();

        List<String> headers = transformer.getHeadersCSV();

        assertEquals(
                List.of(
                        "shop_title",
                        "shop_steam_id",
                        "shop_best_price_ever",
                        "shop_store_id",
                        "shop_current_price",
                        "shop_retail_price",
                        "shop_savings"
                ),
                headers
        );
    }

    @Test
    void testHandleResponseBodyGood() {
        OfferTransformer transformer = new OfferTransformer();
        ObjectMapper mapper = new ObjectMapper();

        String json = getGoodOfferJson();

        byte[] body = json.getBytes();

        List<Offer> result = transformer.handleResponseBody(body, mapper);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(1710000000, result.get(0).cheapestPriceEver().date()),
                () -> assertEquals(1, result.get(0).deals().size())
        );
    }

    @Test
    void testHandleResponseBodyMapperError() throws Exception {
        OfferTransformer transformer = new OfferTransformer();

        ObjectMapper mapper = new ObjectMapper();

        String json = "";


        byte[] body = json.getBytes();

        List<Offer> result = transformer.handleResponseBody(body, mapper);

        assertNull(result);
    }

    private String getGoodOfferJson() {
        return """
            {
              "cheapestPriceEver": {
                "price": "4.99",
                "date": 1710000000
              },
              "info": {
                "title": "Test Game",
                "steamAppID": "12345",
                "thumb": "image.png"
              },
              "deals": [
                {
                  "storeID": "1",
                  "price": "5.99",
                  "retailPrice": "19.99",
                  "savings": "70.00"
                }
              ]
            }
            """;
    }
}