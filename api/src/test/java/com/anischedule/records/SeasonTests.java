package com.anischedule.records;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SeasonTests {

    @Test
    public void checksValidYear() {
        Integer year = 2025;
        Assertions.assertTrue(Season.isValidYear(year));
    }

    @Test
    public void checksInvalidYear() {
        Integer year = 1732;
        Assertions.assertFalse(Season.isValidYear(year));
    }

    @Test
    public void checksValidSeason() {
        String season = "SUMMER";
        Assertions.assertTrue(Season.isValidSeason(season));
    }

    @Test
    public void checksInvalidSeason() {
        String season = "BAD";
        Assertions.assertFalse(Season.isValidSeason(season));
    }

    @Test
	public void createsValidSeason() {
        String season = "SUMMER";
        Integer year = 2025;

        Season actual = new Season(season, year);

        Assertions.assertEquals(season, actual.season());
        Assertions.assertEquals(year, actual.year());
    }

	@Test
    public void defaultsInvalidSeason() {
        Season expected = getCurrentSeason();

        Season actual = new Season("BAD", 1732);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void defaultsNullSeason() {
        Season expected = getCurrentSeason();

        Season actual = new Season(null, null);

        Assertions.assertEquals(expected, actual);
    }

    private Season getCurrentSeason() {
        Date now = Date.from(Instant.now());
        SimpleDateFormat monthFormatter = new SimpleDateFormat("yyyy-MM");
        String[] parts = monthFormatter.format(now).split("-");
        String currentSeason = switch (parts[1]) {
            case "01", "02", "03" -> "WINTER";
            case "04", "05", "06" -> "SPRING";
            case "07", "08", "09" -> "SUMMER";
            default -> "FALL";
        };
        int currentYear = Integer.parseInt(parts[0]);
        return new Season(currentSeason, currentYear);
    }

}
