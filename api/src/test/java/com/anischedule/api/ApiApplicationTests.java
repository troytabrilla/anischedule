package com.anischedule.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {

	@Test
	void contextLoads() {}

    @Test
    void homeLoads() {
        ApiApplication app = new ApiApplication();
        String expected = "AniSchedule API";

        String actual = app.home();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void animeLoads() throws Exception {
        ApiApplication app = new ApiApplication();
        AniListAPI spy = Mockito.spy(AniListAPI.class);
        AniListAPI.Season season = new AniListAPI.Season("SUMMER", 2025);
        JSONObject testResponse = new JSONObject("{\"data\":{\"Page\":{\"media\":[{\"id\":1,\"title\":{\"romaji\":\"romaji\",\"english\":\"english\",\"native\":\"native\"},\"description\":\"description\",\"season\":\"SUMMER\",\"seasonYear\":2025,\"episodes\":12,\"coverImage\":{\"large\":\"large\",\"extraLarge\":\"extraLarge\"},\"nextAiringEpisode\":{\"airingAt\":2,\"episode\":3},\"siteUrl\":\"url\"}],\"pageInfo\":{\"total\":1,\"perPage\":50,\"currentPage\":1,\"lastPage\":1,\"hasNextPage\":false}}}}");
        Mockito.when(spy.callAPI(season, 1)).thenReturn(testResponse);
        ArrayList<AniListAPI.Anime> anime = new ArrayList<>();
        anime.add(new AniListAPI.Anime(
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
        Map<String, Object> expected = new HashMap<>();
        expected.put("anime", anime);

        Map<String, Object> actual = app.anime(season.season(), season.year(), false, spy);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void animeThrowsAPIException() throws Exception {
        ApiApplication app = new ApiApplication();
        AniListAPI spy = Mockito.spy(AniListAPI.class);
        AniListAPI.Season season = new AniListAPI.Season("SUMMER", 2025);
        JSONObject testResponse = new JSONObject("{}");
        Mockito.when(spy.callAPI(season, 1)).thenReturn(testResponse);

        Assertions.assertThrows(AniListAPI.APIException.class, () -> app.anime(season.season(), season.year(), false, spy));
    }

    @Test
    void animeThrowsBadRequestException() throws Exception {
        ApiApplication app = new ApiApplication();
        AniListAPI spy = Mockito.spy(AniListAPI.class);

        Assertions.assertThrows(AniListAPI.BadRequestException.class, () -> app.anime("BAD", 1703, false, spy));
    }

}
