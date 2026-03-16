package models.custom.itad;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import models.custom.Mappable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameInfo(String id,
                       String title,
                       boolean achievements,
                       List<String> tags,
                       List<Review> reviews) implements Mappable {
    @Override
    public List<Map<String, String>> toCsvMaps() {
        List<Map<String, String>> rows = new ArrayList<>();

        String flatTags = (tags != null) ? String.join("; ", tags) : "";

        if (reviews == null || reviews.isEmpty()) {
            rows.add(fillMap(flatTags, null));
        } else {
            for (Review review : reviews) {
                rows.add(fillMap(flatTags, review));
            }
        }
        return rows;
    }

    private Map<String, String> fillMap(String flatTags, Review review) {
        Map<String, String> map = new HashMap<>();
        map.put("itad_id", id);
        map.put("itad_title", title);
        map.put("itad_achievements", String.valueOf(achievements));
        map.put("itad_tags", flatTags);

        if (review != null) {
            map.put("itad_review_source", review.source());
            map.put("itad_review_score", String.valueOf(review.score()));
            map.put("itad_review_url", review.url());
        } else {
            map.put("itad_review_source", "");
            map.put("itad_review_score", "");
            map.put("itad_review_url", "");
        }
        return map;
    }
}
