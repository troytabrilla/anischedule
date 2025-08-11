package com.anischedule.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.anischedule.anilistapi.AniListAPI;
import com.anischedule.exceptions.APIException;
import com.anischedule.exceptions.BadRequestException;
import com.anischedule.records.Anime;
import com.anischedule.records.Season;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.UnifiedJedis;

@RestController
public class AnimeController {
    private final AniListAPI api;

    public AnimeController() {
        this.api = new AniListAPI();
    }

    public AnimeController(AniListAPI api) {
        this.api = api != null ? api : new AniListAPI();
    }

    // TODO refactor UnifiedJedis into Cache class (cache folder)
    // TODO figure out dependency injection for cache and anilistapi classes
    @RequestMapping("/anime")
    public Map<String, Object> anime(
            @RequestParam(required = false) String season,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false, defaultValue = "false") Boolean cached)
    throws Exception {
        Season targetSeason = validateParams(season, year);

        if (cached == true) {
            Map<String, Object> cachedResults = getCachedResults(targetSeason);
            if (cachedResults != null) {
                return cachedResults;
            }
        }

        ArrayList<Anime> animeList = api.loadSeasonAnime(targetSeason);

        Map<String, Object> results = new HashMap<>();
        results.put("anime", animeList);

        if (cached == true) {
            cacheResults(targetSeason, results);
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

    private Map<String, Object> getCachedResults(Season season) {
        Map<String, Object> results = null;
        try (UnifiedJedis jedis = new UnifiedJedis(getRedisUrl())) {
            String cacheKey = "anime|" + season.season() + "|" + season.year();
            String cached = jedis.get(cacheKey);
            ObjectMapper mapper = new ObjectMapper();
            results = mapper.readValue(cached, new TypeReference<Map<String, Object>>() {
            });
            System.out.println("Cache Hit");
        } catch (Exception e) {
            System.out.println("Cache Miss");
            System.err.println("Could not fetch cached results: " + e);
        }
        return results;
    }

    private void cacheResults(Season season, Map<String, Object> results) {
        try (UnifiedJedis jedis = new UnifiedJedis(getRedisUrl())) {
            String cacheKey = "anime|" + season.season() + "|" + season.year();
            ObjectMapper mapper = new ObjectMapper();
            jedis.set(cacheKey, mapper.writeValueAsString(results));
            jedis.pexpire(cacheKey, 86400 * 1000);
        } catch (Exception e) {
            System.err.println("Could not cache results: " + e);
        }
    }

    private String getRedisUrl() {
        String url = System.getenv("REDIS_URL");
        return url != null ? url : "redis://localhost:6379";
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
