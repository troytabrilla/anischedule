package com.anischedule.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AniListAPI {

    public ArrayList<Anime> loadSeasonAnime(Season season) throws APIException, JsonProcessingException, IOException {
        ArrayList<Anime> animeList = new ArrayList<>();

        int currentPage = 1;
        boolean hasNextPage;
        do {
            JSONObject json = callAPI(season, currentPage);
            if (json == null) {
                throw new APIException("Invalid API Response", null);
            }
            if (json.isNull("data")) {
                throw new APIException("Invalid Format", Arrays.asList("No Data"));
            }

            JSONObject data = json.getJSONObject("data");
            if (data.isNull("Page")) {
                throw new APIException("Invalid Format", Arrays.asList("No Page"));
            }

            JSONObject page = data.getJSONObject("Page");
            if (page.isNull("media")) {
                throw new APIException("Invalid Format", Arrays.asList("No Media"));
            }
            if (page.isNull("pageInfo")) {
                throw new APIException("Invalid Format", Arrays.asList("No Page Info"));
            }

            JSONArray media = page.getJSONArray("media");
            for (int i = 0; i < media.length(); i++) {
                JSONObject entry = media.getJSONObject(i);
                JSONObject title = entry.isNull("title") ? null : entry.getJSONObject("title");
                JSONObject nextAiringEpisode = entry.isNull("nextAiringEpisode") ? null : entry.getJSONObject("nextAiringEpisode");
                JSONObject coverImage = entry.isNull("coverImage") ? null : entry.getJSONObject("coverImage");
                Anime anime = new Anime(
                    entry.isNull("id") ? null : entry.getInt("id"),
                    title == null || title.isNull("native") ? null : title.getString("native"),
                    title == null || title.isNull("romaji") ? null : title.getString("romaji"),
                    title == null || title.isNull("english") ? null : title.getString("english"),
                    entry.isNull("description") ? null : entry.getString("description"),
                    entry.isNull("episodes") ? null : entry.getInt("episodes"),
                    entry.isNull("season") ? null : entry.getString("season"),
                    entry.isNull("seasonYear") ? null : entry.getInt("seasonYear"),
                    nextAiringEpisode == null || nextAiringEpisode.isNull("airingAt") ? null : nextAiringEpisode.getInt("airingAt"),
                    nextAiringEpisode == null || nextAiringEpisode.isNull("episode") ? null : nextAiringEpisode.getInt("episode"),
                    entry.isNull("siteUrl") ? null : entry.getString("siteUrl"),
                    coverImage == null || coverImage.isNull("extraLarge") ? null : coverImage.getString("extraLarge"),
                    coverImage == null || coverImage.isNull("large") ? null : coverImage.getString("large")
                );
                animeList.add(anime);
            }

            currentPage++;

            JSONObject pageInfo = page.getJSONObject("pageInfo");
            hasNextPage = pageInfo.isNull("hasNextPage") ? false : pageInfo.getBoolean("hasNextPage");
        } while (hasNextPage == true);

        return animeList;
    }

    public JSONObject callAPI(Season season, int page) throws JsonProcessingException, IOException {
        String query = loadQuery();
        HttpPost request = createRequest(query, season, page);

        HttpClient client = HttpClients.createDefault();
        return client.execute(request, response -> {
            int code = response.getCode();
            String res = EntityUtils.toString(response.getEntity());
            if (code != 200) {
                System.err.println("Invalid API response: " + code + " " + res);
                return null;
            }
            JSONObject result = new JSONObject(res);
            return result;
        });
    }

    private String loadQuery() throws IOException {
        InputStream queryStream = AniListAPI.class.getResourceAsStream("/queries/anischedule-query.graphql");
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(queryStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private HttpPost createRequest(String query, Season season, int page) throws JsonProcessingException {
        HttpPost request = new HttpPost("https://graphql.anilist.co");
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept", "application/json");

        Map<String, Object> variables = new HashMap<>();
        variables.put("season", season.season());
        variables.put("seasonYear", season.year());
        variables.put("page", page);

        Map<String, Object> body = new HashMap<>();
        body.put("query", query);
        body.put("variables", variables);

        String json = new ObjectMapper().writeValueAsString(body);
        request.setEntity(new StringEntity(json));

        return request;
    }

    public static record Anime(
        Integer id,
        String nativeTitle,
        String romajiTitle,
        String englishTitle,
        String description,
        Integer episodes,
        String season,
        Integer year,
        Integer nextEpisodeAiringAt,
        Integer nextAiringEpisode,
        String url,
        String thumbnailExtraLarge,
        String thumbnailLarge
    ) {}

    public static record Season(String season, Integer year) {

        public Season(String season, Integer year) {
            this.season = season != null && isValidSeason(season) ? season : getCurrentSeason();
            this.year = year != null && isValidYear(year) ? year : getCurrentYear();
        }

        public static boolean isValidSeason(String season) {
            Set<String> validSeasons = new HashSet<>();
            validSeasons.add("WINTER");
            validSeasons.add("SPRING");
            validSeasons.add("SUMMER");
            validSeasons.add("FALL");
            return validSeasons.contains(season);
        }

        private static String getCurrentSeason() {
            Date now = getCurrentDate();
            SimpleDateFormat formatter = new SimpleDateFormat("MM");
            String month = formatter.format(now);
            return switch (month) {
                case "01", "02", "03" -> "WINTER";
                case "04", "05", "06" -> "SPRING";
                case "07", "08", "09" -> "SUMMER";
                default -> "FALL";
            };
        }

        public static boolean isValidYear(Integer year) {
            return year >= 1854 && year <= getCurrentYear();
        }

        private static int getCurrentYear() {
            Date now = getCurrentDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            String currentYear = formatter.format(now);
            return Integer.parseInt(currentYear);
        }

        private static Date getCurrentDate() {
            return Date.from(Instant.now());
        }

    }

    public static class APIException extends BaseException {

        public APIException(String msg, List<Object> details) {
            super(msg, details);
        }

    }

    public static class BadRequestException extends BaseException {

        public BadRequestException(String msg, List<Object> details) {
            super(msg, details);
        }

    }

    public static class BaseException extends Exception {
        public String msg;
        public List<Object> details;

        public BaseException(String msg, List<Object> details) {
            super(msg);
            this.msg = msg;
            this.details = details;
        }

        @Override
        public String toString() {
            return super.toString() + " - " + this.details;
        }
    }

}
