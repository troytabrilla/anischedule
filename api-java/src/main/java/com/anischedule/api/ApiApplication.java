package com.anischedule.api;

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

    private final String[] allowedOrigins = new String[]{
        "http://localhost:5173", // vite dev server
        "http://localhost:3000", // nginx server
        "http://localhost:8000", // kong gateway proxy
        "http://localhost:80", // minikube gateway proxy
    };

    @GetMapping("/api/v1")
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
                registry.addMapping("/api/v1").allowedOrigins(allowedOrigins);
                registry.addMapping("/api/v1/anime").allowedOrigins(allowedOrigins);
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
