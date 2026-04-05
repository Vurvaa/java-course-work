package connector.transformer.transformers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.custom.steam.GameData;
import connector.transformer.Transformer;

import java.io.IOException;
import java.util.List;

public class SteamTransformer implements Transformer<GameData> {
    @Override
    public String getName() {
        return "steam";
    }

    @Override
    public List<GameData> handleResponseBody(byte[] body, ObjectMapper mapper) {
        final String subField = "data";
        final String steamID = "3764200";

        try {
            JsonNode root = mapper.readTree(body);
            JsonNode results = root.get(steamID).get(subField);

            GameData offer = mapper.readValue(results.toString(), GameData.class);

            return List.of(offer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    @Override
    public List<String> getHeadersCSV() {
        return List.of(
                "steam_appid",
                "steam_name",
                "steam_type",
                "steam_description",
                "steam_currency",
                "steam_initial_price",
                "steam_discount",
                "steam_steam_final_formatted"
        );
    }
}
