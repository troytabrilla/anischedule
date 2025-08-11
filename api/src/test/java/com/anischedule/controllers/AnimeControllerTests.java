package com.anischedule.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.anischedule.anilistapi.AniListAPI;
import com.anischedule.exceptions.APIException;
import com.anischedule.exceptions.BadRequestException;
import com.anischedule.records.Anime;
import com.anischedule.records.Season;

@SuppressWarnings("unchecked")
public class AnimeControllerTests {

    private AniListAPI anilistApi;
    private AnimeController controller;

    @Mock
    private HttpClient mockClient;
    
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        anilistApi = new AniListAPI(mockClient);
        controller = new AnimeController(anilistApi);
    }

    @Test
    public void animeLoads() throws Exception {
        Season season = new Season("SUMMER", 2025);
        JSONObject testResponse = new JSONObject("{\"data\":{\"Page\":{\"media\":[{\"id\":1,\"title\":{\"romaji\":\"romaji\",\"english\":\"english\",\"native\":\"native\"},\"description\":\"description\",\"season\":\"SUMMER\",\"seasonYear\":2025,\"episodes\":12,\"coverImage\":{\"large\":\"large\",\"extraLarge\":\"extraLarge\"},\"nextAiringEpisode\":{\"airingAt\":2,\"episode\":3},\"siteUrl\":\"url\"}],\"pageInfo\":{\"total\":1,\"perPage\":50,\"currentPage\":1,\"lastPage\":1,\"hasNextPage\":false}}}}");
        Mockito.when(mockClient.execute(Mockito.any(ClassicHttpRequest.class), Mockito.any(HttpClientResponseHandler.class))).thenReturn(testResponse);

        ArrayList<Anime> anime = new ArrayList<>();
        anime.add(new Anime(
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

        Map<String, Object> actual = controller.anime(season.season(), season.year(), false);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void animeThrowsAPIException() throws Exception {
        Season season = new Season("SUMMER", 2025);
        JSONObject testResponse = new JSONObject("{}");
        Mockito.when(mockClient.execute(Mockito.any(ClassicHttpRequest.class), Mockito.any(HttpClientResponseHandler.class))).thenReturn(testResponse);


        Exception e = Assertions.assertThrows(APIException.class, () -> controller.anime(season.season(), season.year(), false));
        Assertions.assertTrue(e.toString().contains("Invalid Format - [No Data]"));
    }

    @Test
    public void animeThrowsBadRequestException() throws Exception {
        Exception e = Assertions.assertThrows(BadRequestException.class, () -> controller.anime("BAD", 1703, false));
        Assertions.assertTrue(e.toString().contains("Invalid Season: BAD"));
    }

    // TODO test caching (in its own file)
}
