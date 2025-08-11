package com.anischedule.records;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public record Season(String season, Integer year) {

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
