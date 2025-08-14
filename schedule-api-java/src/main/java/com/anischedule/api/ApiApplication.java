package com.anischedule.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@SpringBootApplication
@ComponentScan(basePackages = "com.anischedule")
public class ApiApplication {

    private final static String BASE_UI_URL = System.getenv("BASE_UI_URL");
    private final String[] allowedOrigins = new String[]{
        exists(BASE_UI_URL) ? BASE_UI_URL.trim() : "http://localhost:5173", // default vite dev server
    };

    @GetMapping("/v1")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "AniSchedule API");
        return response;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                System.out.println("Allowed origins: " + Arrays.toString(allowedOrigins));
                registry.addMapping("/v1/**").allowedOrigins(allowedOrigins);
            }
        };
    }

    private boolean exists(String s) {
        return s != null && !s.isBlank();
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
