package models.custom.itad;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameInfoTest {

    @Test
    void testToCsvMapsWithOneReview() {
        Review review = new Review(90, "Steam", 100, "https://example.com");

        GameInfo gameInfo = new GameInfo(
                "game-id",
                "Test Game",
                true,
                List.of("Action", "RPG"),
                List.of(review)
        );

        List<Map<String, String>> result = gameInfo.toCsvMaps();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("https://example.com", result.get(0).get("itad_review_url"))
        );
    }

    @Test
    void testToCsvMapsWithTwoReviews() {
        Review review1 = new Review(90, "Steam", 100, "https://steam.com");
        Review review2 = new Review(85, "Metacritic", 50, "https://metacritic.com");

        GameInfo gameInfo = new GameInfo(
                "game-id",
                "Test Game",
                false,
                List.of("Action", "Adventure"),
                List.of(review1, review2)
        );

        List<Map<String, String>> result = gameInfo.toCsvMaps();

        assertAll(
                () -> assertEquals(2, result.size()),

                () -> assertEquals("game-id", result.get(0).get("itad_id")),
                () -> assertEquals("https://steam.com", result.get(0).get("itad_review_url")),

                () -> assertEquals("game-id", result.get(1).get("itad_id")),
                () -> assertEquals("https://metacritic.com", result.get(1).get("itad_review_url"))
        );
    }

    @Test
    void testToCsvMapsWithEmptyReviews() {
        GameInfo gameInfo = new GameInfo(
                "game-id",
                "Test Game",
                true,
                List.of("Strategy", "Indie"),
                List.of()
        );

        List<Map<String, String>> result = gameInfo.toCsvMaps();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("game-id", result.get(0).get("itad_id")),
                () -> assertEquals("", result.get(0).get("itad_review_score")),
                () -> assertEquals("", result.get(0).get("itad_review_url"))
        );
    }

    @Test
    void testToCsvMapsWithNullReviews() {
        GameInfo gameInfo = new GameInfo(
                "game-id",
                "Test Game",
                true,
                List.of("Strategy", "Indie"),
                null
        );

        List<Map<String, String>> result = gameInfo.toCsvMaps();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("", result.get(0).get("itad_review_source")),
                () -> assertEquals("", result.get(0).get("itad_review_score")),
                () -> assertEquals("", result.get(0).get("itad_review_url"))
        );
    }

    @Test
    void testToCsvMapsWithNullTags() {
        Review review = new Review(90, "Steam", 100, "https://example.com");

        GameInfo gameInfo = new GameInfo(
                "game-id",
                "Test Game",
                true,
                null,
                List.of(review)
        );

        List<Map<String, String>> result = gameInfo.toCsvMaps();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("", result.get(0).get("itad_tags"))
        );
    }

    @Test
    void testRecordFields() {
        Review review = new Review(90, "Steam", 100, "https://example.com");

        GameInfo gameInfo = new GameInfo(
                "game-id",
                "Test Game",
                true,
                List.of("Action"),
                List.of(review)
        );

        assertAll(
                () -> assertEquals("game-id", gameInfo.id()),
                () -> assertTrue(gameInfo.achievements()),
                () -> assertEquals(List.of(review), gameInfo.reviews())
        );
    }
}