package models.custom.games;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import models.custom.Mappable;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Game(String slug,
                   String name,
                   String released,
                   double rating,
                   List<Genre> genres) implements Mappable {
    @Override
    public List<Map<String, String>> toCsvMaps() {
        return genres.stream().map(g -> Map.of(
                "top_games_slug", slug,
                "top_games_name", name,
                "top_games_released", released,
                "top_games_rating", String.valueOf(rating),
                "top_games_genre_name", g.name()
        )).toList();
    }
}
