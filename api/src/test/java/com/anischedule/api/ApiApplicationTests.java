package com.anischedule.api;

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
        String expected = "AniSchedule API";

        String actual = app.home();

        Assertions.assertEquals(expected, actual);
    }

}
