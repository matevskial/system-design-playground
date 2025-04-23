package com.matevskial.systemdesignplayground.urlshortener.controller;

import com.matevskial.systemdesignplayground.urlshortener.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
class V1UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shorten(@RequestParam String originalUrl) {
        try {
            String shortenedUrl = urlShortenerService.getOrCreateShortenedUrl(originalUrl);
            return ResponseEntity.created(URI.create(shortenedUrl)).body(shortenedUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error while shortening URL");
        }
    }

    @GetMapping("/{shortened}")
    public ResponseEntity<String> redirectToOriginalUrl(@PathVariable String shortened) {
        try {
            Optional<String> originalUrl = urlShortenerService.getOriginalUrl(shortened);
            if (originalUrl.isPresent()) {
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl.get())).build();
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error while redirecting to original URL");
        }
    }
}
