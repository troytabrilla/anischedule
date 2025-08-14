package com.anischedule.anilistapi;

import java.util.ArrayList;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.anischedule.exceptions.APIException;
import com.anischedule.records.Anime;
import com.anischedule.records.Season;

@SuppressWarnings("unchecked")
class AniListAPITests {

    @Mock
    private HttpClient mockClient;

    @InjectMocks
    private AniListAPI anilistApi;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void loadsValidSeasonAnime() throws Exception {
        AniListAPI spy = Mockito.spy(anilistApi);
        Season season = new Season("SUMMER", 2025);
        JSONObject testResponse = new JSONObject("{\"data\":{\"Page\":{\"media\":[{\"id\":1,\"title\":{\"romaji\":\"romaji\",\"english\":\"english\",\"native\":\"native\"},\"description\":\"description\",\"season\":\"SUMMER\",\"seasonYear\":2025,\"episodes\":12,\"coverImage\":{\"large\":\"large\",\"extraLarge\":\"extraLarge\"},\"nextAiringEpisode\":{\"airingAt\":2,\"episode\":3},\"siteUrl\":\"url\",\"isAdult\":false}],\"pageInfo\":{\"total\":1,\"perPage\":50,\"currentPage\":1,\"lastPage\":1,\"hasNextPage\":false}}}}");
        Mockito.when(mockClient.execute(Mockito.any(ClassicHttpRequest.class), Mockito.any(HttpClientResponseHandler.class))).thenReturn(testResponse);

        ArrayList<Anime> expected = new ArrayList<>();
        expected.add(new Anime(
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
            "large",
            false
        ));

        ArrayList<Anime> actual = spy.loadSeasonAnime(season, false);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void throwsInvalidSeason() throws Exception {
        AniListAPI spy = Mockito.spy(anilistApi);
        Season season = new Season("SUMMER", 2025);
        JSONObject testResponse = new JSONObject("{}");
        Mockito.when(mockClient.execute(Mockito.any(ClassicHttpRequest.class), Mockito.any(HttpClientResponseHandler.class))).thenReturn(testResponse);

        String expected = "Invalid Format";

        Exception actual = Assertions.assertThrows(APIException.class, () -> spy.loadSeasonAnime(season, false));
        
        Assertions.assertTrue(actual.getMessage().contains(expected));
    }

    @Test
    public void throwsEmptySeason() throws Exception {
        AniListAPI spy = Mockito.spy(anilistApi);
        Season season = new Season("SUMMER", 2025);
        JSONObject testResponse = new JSONObject("{\"data\":{\"Page\":{\"media\":[],\"pageInfo\":{\"total\":1,\"perPage\":50,\"currentPage\":1,\"lastPage\":1,\"hasNextPage\":false}}}}");
        Mockito.when(mockClient.execute(Mockito.any(ClassicHttpRequest.class), Mockito.any(HttpClientResponseHandler.class))).thenReturn(testResponse);

        ArrayList<Anime> expected = new ArrayList<>();

        ArrayList<Anime> actual = spy.loadSeasonAnime(season, false);

        Assertions.assertEquals(expected, actual);
    }

}
