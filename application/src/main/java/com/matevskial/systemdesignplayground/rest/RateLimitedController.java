package com.matevskial.systemdesignplayground.rest;

import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * Controller exposing rate-limited endpoints for testing custom ratelimit implementations
 */
@RestController
@RequestMapping("/api/v1/ratelimited")
public class RateLimitedController {

    @GetMapping("/{configuration}")
    public String rateLimited(@PathVariable String configuration, @RequestParam(required = false) String delay) {
        Duration duration;
        try {
            duration = Duration.parse(delay);
        } catch (Exception e) {
            duration = Duration.ofSeconds(1);
        }

        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return configuration;
    }
}
