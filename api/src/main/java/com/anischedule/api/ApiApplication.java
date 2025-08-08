package com.anischedule.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.UnifiedJedis;

@RestController
@SpringBootApplication
public class ApiApplication {

    @RequestMapping("/")
    public String home() {
        return "AniSchedule API";
    }

    @RequestMapping("/anime")
    public Map<String, Object> anime(
        @RequestParam(required=false) String season,
        @RequestParam(required=false) Integer year,
        @RequestParam(required=false, defaultValue="false") Boolean cache
    ) throws Exception {
        AniListAPI api = new AniListAPI();
        return anime(season, year, cache, api);
    }

    public Map<String, Object> anime(
        String season,
        Integer year,
        Boolean cache,
        AniListAPI api
    ) throws Exception {
        validateParams(season, year);

        AniListAPI.Season targetSeason = new AniListAPI.Season(season, year);
        if (cache == true) {
            Map<String, Object> cachedResults = getCachedResults(targetSeason);
            if (cachedResults != null) {
                return cachedResults;
            }
        }

        ArrayList<AniListAPI.Anime> animeList = api.loadSeasonAnime(targetSeason);

        Map<String, Object> results = new HashMap<>();
        results.put("anime", animeList);

        if (cache == true) {
            cacheResults(targetSeason, results);
        }

        return results;
    }

    private void validateParams(String season, Integer year) throws AniListAPI.BadRequestException {
        List<Object> errors = new ArrayList<>();
        if (season != null && !AniListAPI.Season.isValidSeason(season)) {
            errors.add("Invalid Season: " + season);
        }
        if (year != null && !AniListAPI.Season.isValidYear(year)) {
            errors.add("Invalid Year: " + year);
        }
        if (!errors.isEmpty()) {
            throw new AniListAPI.BadRequestException("Bad Request", errors);
        }
    }

    private Map<String, Object> getCachedResults(AniListAPI.Season season) {
        Map<String, Object> results = null;
        try (UnifiedJedis jedis = new UnifiedJedis(getRedisUrl())) {
            String cacheKey = "anime|" + season.season() + "|" + season.year();
            String cached = jedis.get(cacheKey);
            ObjectMapper mapper = new ObjectMapper();
            results = mapper.readValue(cached, new TypeReference<Map<String, Object>>(){});
            System.out.println("Cache Hit");
        } catch (Exception e) {
            System.out.println("Cache Miss");
            System.err.println("Could not fetch cached results: " + e);
        }
        return results;
    }

    private void cacheResults(AniListAPI.Season season, Map<String, Object> results) {
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
    @ExceptionHandler(AniListAPI.BadRequestException.class)
    public Map<String, Object> handleBadRequestException(AniListAPI.BadRequestException e) {
        System.err.println("Bad request: " + e);

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 400);
        response.put("error", e.msg);
        response.put("details", e.details);
        return response;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(AniListAPI.APIException.class)
    public Map<String, Object> handleAPIException(AniListAPI.APIException e) {
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

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
