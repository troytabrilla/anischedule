package com.anischedule.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.anischedule.anilistapi.AniListAPI;
import com.anischedule.cache.APICache;
import com.anischedule.exceptions.APIException;
import com.anischedule.exceptions.BadRequestException;
import com.anischedule.records.Anime;
import com.anischedule.records.Season;

@RestController
public class AnimeController {
    private final AniListAPI api;
    private final APICache cache;

    public AnimeController() {
        this.api = new AniListAPI();
        this.cache = new APICache();
    }

    public AnimeController(AniListAPI api, APICache cache) {
        this.api = api != null ? api : new AniListAPI();
        this.cache = cache != null ? cache : new APICache();
    }

    @GetMapping("/v1/anime")
    public Map<String, Object> anime(
        @RequestParam(required = false) String season,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false, defaultValue = "false") Boolean includeAdultContent,
        @RequestParam(required = false, defaultValue = "true") Boolean cached
    ) throws Exception {
        Season targetSeason = validateParams(season, year);
        String cacheKey = getCacheKey(targetSeason, includeAdultContent);

        if (cached == true) {
            Map<String, Object> cachedResults = cache.get(cacheKey);
            if (cachedResults != null) {
                return cachedResults;
            }
        }

        ArrayList<Anime> animeList = api.loadSeasonAnime(targetSeason, includeAdultContent);

        Map<String, Object> results = new HashMap<>();
        results.put("anime", animeList);

        if (cached == true) {
            cache.set(cacheKey, results);
        }

        return results;
    }

    private Season validateParams(String season, Integer year) throws BadRequestException {
        List<Object> errors = new ArrayList<>();
        if (season != null && !Season.isValidSeason(season)) {
            errors.add("Invalid Season: " + season);
        }
        if (year != null && !Season.isValidYear(year)) {
            errors.add("Invalid Year: " + year);
        }
        if (!errors.isEmpty()) {
            throw new BadRequestException("Bad Request", errors);
        }
        return new Season(season, year);
    }

    private String getCacheKey(Season season, boolean includeAdultContent) {
        return "anime|" + season.season() + "|" + season.year() + "|" + includeAdultContent;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public Map<String, Object> handleBadRequestException(BadRequestException e) {
        System.err.println("Bad request: " + e);

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 400);
        response.put("error", e.getMessage());
        response.put("details", e.getDetails());
        return response;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(APIException.class)
    public Map<String, Object> handleAPIException(APIException e) {
        System.err.println("API exception: " + e);

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 500);
        response.put("error", "AniList API Error");
        return response;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleInternalServerError(Exception e) {
        System.err.println("Internal server error: " + e);

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 500);
        response.put("error", "Internal Server Error");
        return response;
    }
}
