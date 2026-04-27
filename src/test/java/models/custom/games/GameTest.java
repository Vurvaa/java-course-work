package models.custom.games;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTest {

    @Test
    void testToCsvMapsWithOneGenre() {
        Genre genre = mock(Genre.class);
        when(genre.name()).thenReturn("Action");

        Game game = new Game(
                "gta-v",
                "Grand Theft Auto V",
                "2013-09-17",
                4.5,
                List.of(genre)
        );

        List<Map<String, String>> result = game.toCsvMaps();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("gta-v", result.get(0).get("top_games_slug")),
                () -> assertEquals("Grand Theft Auto V", result.get(0).get("top_games_name")),
                () -> assertEquals("2013-09-17", result.get(0).get("top_games_released")),
                () -> assertEquals("4.5", result.get(0).get("top_games_rating")),
                () -> assertEquals("Action", result.get(0).get("top_games_genre_name"))
        );
    }

    @Test
    void testToCsvMapsWithTwoGenres() {
        Genre genre1 = mock(Genre.class);
        Genre genre2 = mock(Genre.class);

        when(genre1.name()).thenReturn("Action");
        when(genre2.name()).thenReturn("RPG");

        Game game = new Game(
                "witcher-3",
                "The Witcher 3",
                "2015-05-18",
                4.8,
                List.of(genre1, genre2)
        );

        List<Map<String, String>> result = game.toCsvMaps();

        assertAll(
                () -> assertEquals(2, result.size()),

                () -> assertEquals("witcher-3", result.get(0).get("top_games_slug")),
                () -> assertEquals("The Witcher 3", result.get(0).get("top_games_name")),
                () -> assertEquals("2015-05-18", result.get(0).get("top_games_released")),
                () -> assertEquals("4.8", result.get(0).get("top_games_rating")),
                () -> assertEquals("Action", result.get(0).get("top_games_genre_name")),

                () -> assertEquals("witcher-3", result.get(1).get("top_games_slug")),
                () -> assertEquals("The Witcher 3", result.get(1).get("top_games_name")),
                () -> assertEquals("2015-05-18", result.get(1).get("top_games_released")),
                () -> assertEquals("4.8", result.get(1).get("top_games_rating")),
                () -> assertEquals("RPG", result.get(1).get("top_games_genre_name"))
        );
    }

    @Test
    void testToCsvMapsWithEmptyGenres() {
        Game game = new Game(
                "some-game",
                "Some Game",
                "2024-01-01",
                3.5,
                List.of()
        );

        List<Map<String, String>> result = game.toCsvMaps();

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 1.5, 4.9, 5.0})
    void testDifferentRatings(double rating) {
        Genre genre = mock(Genre.class);
        when(genre.name()).thenReturn("Adventure");

        Game game = new Game(
                "game",
                "Game",
                "now",
                rating,
                List.of(genre)
        );

        List<Map<String, String>> result = game.toCsvMaps();

        assertEquals(String.valueOf(rating), result.get(0).get("top_games_rating"));
    }

    @Test
    void testRecordFields() {
        Genre genre = mock(Genre.class);

        Game game = new Game(
                "minecraft",
                "Minecraft",
                "2011-11-18",
                4.7,
                List.of(genre)
        );

        assertAll(
                () -> assertEquals("minecraft", game.slug()),
                () -> assertEquals("Minecraft", game.name()),
                () -> assertEquals("2011-11-18", game.released()),
                () -> assertEquals(4.7, game.rating()),
                () -> assertEquals(List.of(genre), game.genres())
        );
    }
}