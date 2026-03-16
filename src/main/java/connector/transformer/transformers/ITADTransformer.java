package connector.transformer.transformers;

import com.fasterxml.jackson.databind.ObjectMapper;
import connector.models.custom.itad.GameInfo;
import connector.transformer.Transformer;

import java.io.IOException;
import java.util.List;

public class ITADTransformer implements Transformer<GameInfo> {
    @Override
    public String getKey() {
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
}
