package connector.transformer.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.custom.offers.Offer;
import connector.transformer.Transformer;

import java.io.IOException;
import java.util.List;

public class OfferTransformer implements Transformer<Offer> {
    @Override
    public String getName() {
        return "shop";
    }

    @Override
    public List<Offer> handleResponseBody(byte[] body, ObjectMapper mapper) {
        try {
            Offer offer = mapper.readValue(body, Offer.class);
            return List.of(offer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    @Override
    public List<String> getHeadersCSV() {
        return List.of(
                "shop_title",
                "shop_steam_id",
                "shop_best_price_ever",
                "shop_store_id",
                "shop_current_price",
                "shop_retail_price",
                "shop_savings"
        );
    }
}
