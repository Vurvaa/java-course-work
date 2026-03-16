package connector.models.custom.steam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameData(String type,
                       String name,
                       int steam_appid,
                       String short_description,
                       Pries price_overview) {
}
