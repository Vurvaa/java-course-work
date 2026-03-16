package connector.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import connector.models.service.notes.ModelJSON;
import connector.transformer.*;
import connector.transformer.transformers.GamesTransformer;
import connector.transformer.transformers.ITADTransformer;
import connector.transformer.transformers.OfferTransformer;
import connector.transformer.transformers.SteamTransformer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DataTransformer {
    private final List<Transformer> transformers;
    private final ObjectMapper mapper;

    public DataTransformer() {
        mapper = new ObjectMapper();
        transformers = List.of(new GamesTransformer(),
                                new ITADTransformer(),
                                new OfferTransformer(),
                                new SteamTransformer());
    }

    public ModelJSON<?> transformInJSON(byte[] body, String nameAPI) {
        if (body == null || nameAPI == null)
            throw new IllegalArgumentException("response body or API name can not be null");

        LocalDateTime createdTime = LocalDateTime.now();

        return new ModelJSON<>(UUID.randomUUID(), createdTime.toString(), nameAPI, transformData(body, nameAPI));
    }

    public void transformInCSV() {

    }

    private List<?> transformData(byte[] body, String nameAPI) {
        List<?> data = null;
        for (var transformer : transformers) {
            if (nameAPI.equals(transformer.getKey()))
                data = transformer.handleResponseBody(body, mapper);
        }

        if (data == null)
            throw new IllegalArgumentException("unknown API or transformer");

        return data;
    }
}
