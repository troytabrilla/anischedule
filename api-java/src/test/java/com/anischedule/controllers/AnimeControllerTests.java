package com.anischedule.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

public class AnimeControllerTests {

    private AnimeController controller;

    @Mock
    private AniListAPI mockAnilistApi;

    @Mock
    private APICache mockCache;
    
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        controller = new AnimeController(mockAnilistApi, mockCache);
    }

    @Test
    public void animeLoads() throws Exception {
        Season season = new Season("SUMMER", 2025);
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
            "large",
            false
        ));
        Map<String, Object> expected = new HashMap<>();
        expected.put("anime", anime);
        Mockito.when(mockAnilistApi.loadSeasonAnime(season)).thenReturn(anime);

        Map<String, Object> actual = controller.anime(season.season(), season.year(), false);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void animeThrowsAPIException() throws Exception {
        Season season = new Season("SUMMER", 2025);
        Mockito.when(mockAnilistApi.loadSeasonAnime(season)).thenThrow(new APIException("Invalid Format", Arrays.asList("No Data")));

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
            "large",
            false
        ));
        Map<String, Object> expected = new HashMap<>();
        expected.put("anime", anime);
        Mockito.when(mockAnilistApi.loadSeasonAnime(season)).thenReturn(anime);
        Mockito.when(mockCache.get(key)).thenReturn(null);
        Mockito.when(mockCache.set(key, expected)).thenReturn(true);

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
            "large",
            false
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
