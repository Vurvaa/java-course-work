package connector.transformer.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;
import connector.models.custom.offers.Offer;
import connector.transformer.Transformer;

import java.io.IOException;
import java.util.List;

public class OfferTransformer implements Transformer<Offer> {
    @Override
    public String getKey() {
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
}
