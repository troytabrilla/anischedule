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
import com.anischedule.cache.APICache;
import com.anischedule.exceptions.APIException;
import com.anischedule.exceptions.BadRequestException;
import com.anischedule.records.Anime;
import com.anischedule.records.Season;

@SuppressWarnings("unchecked")
public class AnimeControllerTests {

    private AnimeController controller;
    private AniListAPI anilistApi;

    @Mock
    private HttpClient mockClient;

    @Mock
    private APICache mockCache;
    
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        anilistApi = new AniListAPI(mockClient);
        controller = new AnimeController(anilistApi, mockCache);
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


        Exception actual = Assertions.assertThrows(APIException.class, () -> controller.anime(season.season(), season.year(), false));
        Assertions.assertTrue(actual.toString().contains("Invalid Format - [No Data]"));
    }

    @Test
    public void animeThrowsBadRequestException() throws Exception {
        Exception actual = Assertions.assertThrows(BadRequestException.class, () -> controller.anime("BAD", 1703, false));
        Assertions.assertTrue(actual.toString().contains("Invalid Season: BAD"));
    }

    @Test
    public void animeCachesResults() throws Exception {
        Season season = new Season("SUMMER", 2025);
        String key = "anime|SUMMER|2025";
        JSONObject testResponse = new JSONObject("{\"data\":{\"Page\":{\"media\":[{\"id\":1,\"title\":{\"romaji\":\"romaji\",\"english\":\"english\",\"native\":\"native\"},\"description\":\"description\",\"season\":\"SUMMER\",\"seasonYear\":2025,\"episodes\":12,\"coverImage\":{\"large\":\"large\",\"extraLarge\":\"extraLarge\"},\"nextAiringEpisode\":{\"airingAt\":2,\"episode\":3},\"siteUrl\":\"url\"}],\"pageInfo\":{\"total\":1,\"perPage\":50,\"currentPage\":1,\"lastPage\":1,\"hasNextPage\":false}}}}");
        Mockito.when(mockClient.execute(Mockito.any(ClassicHttpRequest.class), Mockito.any(HttpClientResponseHandler.class))).thenReturn(testResponse);
        Mockito.when(mockCache.get(key)).thenReturn(null);
        Mockito.when(mockCache.set(key, testResponse.toString())).thenReturn(true);

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

        Map<String, Object> actual = controller.anime(season.season(), season.year(), true);

        Assertions.assertEquals(expected, actual);
        Mockito.verify(mockCache, Mockito.times(1)).get(key);
        Mockito.verify(mockCache, Mockito.times(1)).set(key, expected);
    }

    @Test
    public void animeReturnsCachedResults() throws Exception {
        Season season = new Season("SUMMER", 2025);
        String key = "anime|SUMMER|2025";

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
        Mockito.when(mockCache.get(key)).thenReturn(expected);

        Map<String, Object> actual = controller.anime(season.season(), season.year(), true);

        Assertions.assertEquals(expected, actual);
        Mockito.verify(mockCache, Mockito.times(1)).get(key);
        Mockito.verify(mockCache, Mockito.never()).set(key, expected);
    }
}
