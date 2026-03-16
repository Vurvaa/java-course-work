package connector.models.custom.games;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Game(String slug,
                   String name,
                   String released,
                   double rating,
                   List<Genre> genres) {

}
