package connector.transformer.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.custom.itad.GameInfo;
import connector.transformer.Transformer;

import java.io.IOException;
import java.util.List;

public class ITADTransformer implements Transformer<GameInfo> {
    @Override
    public String getName() {
        return "itad";
    }

    @Override
    public List<GameInfo> handleResponseBody(byte[] body, ObjectMapper mapper) {
        try {
            GameInfo game = mapper.readValue(body, GameInfo.class);
            return List.of(game);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    @Override
    public List<String> getHeadersCSV() {
        return List.of(
                "itad_id",
                "itad_title",
                "itad_achievements",
                "itad_tags",
                "itad_review_source",
                "itad_review_score",
                "itad_review_url"
        );
    }
}
