package com.aniket.movie.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public String health() {
        return "Hello & Welcome to Hazelcast Caching Demo";
    }
}
