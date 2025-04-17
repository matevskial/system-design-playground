package com.matevskial.systemdesignplayground.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * Controller exposing rate-limited endpoints for testing custom ratelimit implementations
 */
@RestController
@RequestMapping("/api/v1/ratelimited")
public class RateLimitedController {

    @GetMapping("/{configuration}")
    public ResponseEntity<String> rateLimited(@PathVariable String configuration, @RequestParam(required = false) String delay, @RequestParam(required = false) String status) {
        Duration duration = null;
        try {
            duration = Duration.parse(delay);
        } catch (Exception e) {
            // do nothing
        }

        if (duration != null) {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        HttpStatus httpStatus;
        try {
            httpStatus = HttpStatus.resolve(Integer.parseInt(status));
            if (httpStatus == null) {
                httpStatus = HttpStatus.OK;
            }
        } catch (Exception e) {
            httpStatus = HttpStatus.OK;
        }

        return ResponseEntity.status(httpStatus).body(configuration);
    }
}
