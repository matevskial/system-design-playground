package com.matevskial.systemdesignplayground.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * Controller exposing rate-limited endpoints for testing custom ratelimit implementations
 */
@RestController
@RequestMapping("/api/v1/ratelimited")
public class RateLimitedController {

    @GetMapping
    public String rateLimited(@RequestParam String delay) {
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

        return "RateLimited";
    }
}
