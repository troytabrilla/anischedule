package com.anischedule.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@ComponentScan(basePackages = "com.anischedule")
public class ApiApplication {

    @RequestMapping("/")
    public String home() {
        return "AniSchedule API";
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
