package connector.transformer.transformers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import connector.models.custom.games.Game;
import connector.transformer.Transformer;

import java.io.IOException;
import java.util.List;

public class GamesTransformer implements Transformer<Game> {
    @Override
    public String getKey() {
        return "top_games";
    }

    @Override
    public List<Game> handleResponseBody(byte[] body, ObjectMapper mapper) {
        final String subField = "results";

        try {
            JsonNode root = mapper.readTree(body);
            JsonNode results = root.get(subField);

            List<Game> games = mapper.readValue(results.toString(), new TypeReference<>() {});

            return games;
        } catch (IOException e) {
            e.printStackTrace();

            throw new IllegalStateException("error with reading steam response", e);
        }
    }
}
