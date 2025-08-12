package com.anischedule.anilistapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import com.anischedule.exceptions.APIException;
import com.anischedule.records.Anime;
import com.anischedule.records.Season;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AniListAPI {

    private final HttpClient client;

    public AniListAPI() {
        this.client = HttpClients.createDefault();
    }

    public AniListAPI(HttpClient client) {
        this.client = client != null ? client : HttpClients.createDefault();
    }

    public ArrayList<Anime> loadSeasonAnime(Season season) throws APIException, JsonProcessingException, IOException {
        ArrayList<Anime> animeList = new ArrayList<>();

        int currentPage = 1;
        boolean hasNextPage;
        do {
            JSONObject page = callAPI(season, currentPage);

            processPage(page, animeList);

            JSONObject pageInfo = page.getJSONObject("pageInfo");
            hasNextPage = pageInfo.isNull("hasNextPage") ? false : pageInfo.getBoolean("hasNextPage");
            currentPage++;
        } while (hasNextPage == true);

        return animeList;
    }

    private JSONObject callAPI(Season season, int page) throws JsonProcessingException, IOException, APIException {
        String query = loadQuery();
        HttpPost request = createRequest(query, season, page);

        JSONObject apiResponse = client.execute(request, response -> {
            int code = response.getCode();
            String res = EntityUtils.toString(response.getEntity());
            if (code != 200) {
                System.err.println("Invalid API response: " + code + " " + res);
                return null;
            }
            JSONObject result = new JSONObject(res);
            return result;
        });

        return validateAPIResponse(apiResponse);
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

    private JSONObject validateAPIResponse(JSONObject response) throws APIException {
        if (response == null) {
            throw new APIException("Invalid API Response", null);
        }
        if (response.isNull("data")) {
            throw new APIException("Invalid Format", Arrays.asList("No Data"));
        }

        JSONObject data = response.getJSONObject("data");
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

        return page;
    }

    private void processPage(JSONObject page, ArrayList<Anime> animeList) {
        JSONArray media = page.getJSONArray("media");
        for (int i = 0; i < media.length(); i++) {
            JSONObject entry = media.getJSONObject(i);
            JSONObject title = entry.isNull("title") ? null : entry.getJSONObject("title");
            JSONObject nextAiringEpisode = entry.isNull("nextAiringEpisode") ? null : entry.getJSONObject("nextAiringEpisode");
            // skip anime w/o next airingAt
            if (nextAiringEpisode == null || nextAiringEpisode.isNull("airingAt") || nextAiringEpisode.isNull("episode")) {
                continue;
            }
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
                nextAiringEpisode.isNull("airingAt") ? null : nextAiringEpisode.getInt("airingAt"),
                nextAiringEpisode.isNull("episode") ? null : nextAiringEpisode.getInt("episode"),
                entry.isNull("siteUrl") ? null : entry.getString("siteUrl"),
                coverImage == null || coverImage.isNull("extraLarge") ? null : coverImage.getString("extraLarge"),
                coverImage == null || coverImage.isNull("large") ? null : coverImage.getString("large"),
                entry.isNull("isAdult") ? null : entry.getBoolean("isAdult")
            );
            animeList.add(anime);
        }
    }

}
