package models.custom.steam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import models.custom.Mappable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameData(String type,
                       String name,
                       int steam_appid,
                       String short_description,
                       Pries price_overview) implements Mappable {
    @Override
    public List<Map<String, String>> toCsvMaps() {
        Map<String, String> map = new HashMap<>();

        map.put("steam_appid", String.valueOf(steam_appid));
        map.put("steam_name", name);
        map.put("steam_type", type);
        map.put("steam_description", short_description);

        if (price_overview != null) {
            map.put("steam_currency", price_overview.currency());
            map.put("steam_initial_price", String.valueOf(price_overview.initial()));
            map.put("steam_discount", price_overview.discount_percent() + "%");
        } else {
            map.put("steam_currency", "");
            map.put("steam_initial_price", "");
            map.put("steam_discount", "");
        }

        return List.of(map);
    }
}
