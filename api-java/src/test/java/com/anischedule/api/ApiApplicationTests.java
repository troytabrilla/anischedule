package com.anischedule.api;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {

    private ApiApplication app;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        app = new ApiApplication();
    }

	@Test
	public void contextLoads() {}

    @Test
    public void homeLoads() {
        Map<String, String> expected = new HashMap<>();
        expected.put("message", "AniSchedule API");

        Map<String, String> actual = app.home();

        Assertions.assertEquals(expected, actual);
    }

}
