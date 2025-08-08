package com.anischedule.api;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AniListAPITests {

	@Test
	void testValidSeason() {
        String season = "SUMMER";
        Integer year = 2025;

        AniListAPI.Season actual = new AniListAPI.Season(season, year);

        Assertions.assertEquals(season, actual.season());
        Assertions.assertEquals(year, actual.year());
    }

	@Test
    void testInvalidSeason() {
        AniListAPI.Season expected = getCurrentSeason();

        AniListAPI.Season actual = new AniListAPI.Season("BAD", 1732);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testNullSeason() {
        AniListAPI.Season expected = getCurrentSeason();

        AniListAPI.Season actual = new AniListAPI.Season(null, null);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testValidLoadSeasonAnime() throws Exception {
        AniListAPI spy = Mockito.spy(AniListAPI.class);
        AniListAPI.Season season = new AniListAPI.Season("SUMMER", 2025);
        JSONObject testResponse = new JSONObject("{\"data\":{\"Page\":{\"media\":[{\"id\":1,\"title\":{\"romaji\":\"romaji\",\"english\":\"english\",\"native\":\"native\"},\"description\":\"description\",\"season\":\"SUMMER\",\"seasonYear\":2025,\"episodes\":12,\"coverImage\":{\"large\":\"large\",\"extraLarge\":\"extraLarge\"},\"nextAiringEpisode\":{\"airingAt\":2,\"episode\":3},\"siteUrl\":\"url\"}],\"pageInfo\":{\"total\":1,\"perPage\":50,\"currentPage\":1,\"lastPage\":1,\"hasNextPage\":false}}}}");
        Mockito.when(spy.callAPI(season, 1)).thenReturn(testResponse);
        ArrayList<AniListAPI.Anime> expected = new ArrayList<>();
        expected.add(new AniListAPI.Anime(
            1,
            "native",
            "romaji",
            "english",
            "description",
            12,
            season.season(),
            season.year(),
            2,
            3,
            "url",
            "extraLarge",
            "large"
        ));

        ArrayList<AniListAPI.Anime> actual = spy.loadSeasonAnime(season);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testInvalidLoadSeasonAnime() throws Exception {
        AniListAPI spy = Mockito.spy(AniListAPI.class);
        AniListAPI.Season season = new AniListAPI.Season("SUMMER", 2025);
        JSONObject testResponse = new JSONObject("{}");
        Mockito.when(spy.callAPI(season, 1)).thenReturn(testResponse);

        Assertions.assertThrows(AniListAPI.APIException.class, () -> spy.loadSeasonAnime(season));
    }

    @Test
    void testEmptyLoadSeasonAnime() throws Exception {
        AniListAPI spy = Mockito.spy(AniListAPI.class);
        AniListAPI.Season season = new AniListAPI.Season("SUMMER", 2025);
        JSONObject testResponse = new JSONObject("{\"data\":{\"Page\":{\"media\":[],\"pageInfo\":{\"total\":1,\"perPage\":50,\"currentPage\":1,\"lastPage\":1,\"hasNextPage\":false}}}}");
        Mockito.when(spy.callAPI(season, 1)).thenReturn(testResponse);
        ArrayList<AniListAPI.Anime> expected = new ArrayList<>();

        ArrayList<AniListAPI.Anime> actual = spy.loadSeasonAnime(season);

        Assertions.assertEquals(expected, actual);
    }

    private AniListAPI.Season getCurrentSeason() {
        Date now = Date.from(Instant.now());
        SimpleDateFormat monthFormatter = new SimpleDateFormat("yyyy-MM");
        String[] parts = monthFormatter.format(now).split("-");
        String currentSeason = switch (parts[1]) {
            case "01", "02", "03" -> "WINTER";
            case "04", "05", "06" -> "SPRING";
            case "07", "08", "09" -> "SUMMER";
            default -> "FALL";
        };
        int currentYear = Integer.parseInt(parts[0]);
        return new AniListAPI.Season(currentSeason, currentYear);
    }

}
