package com.anischedule.api;

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

    private final String[] allowedOrigins = new String[]{
        "http://localhost:5173", // vite server
        "http://localhost:3000" // express server
    };

    @GetMapping("/api/v1")
    public String home() {
        return "AniSchedule API";
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/api/v1").allowedOrigins(allowedOrigins);
                registry.addMapping("/api/v1/anime").allowedOrigins(allowedOrigins);
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
