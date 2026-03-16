package connector.models.custom.itad;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameInfo(String id,
                       String title,
                       boolean achievements,
                       List<String> tags,
                       List<Review> reviews) {

}
