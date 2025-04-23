package com.matevskial.systemdesignplayground.urlshortener.web.controller;

import com.matevskial.systemdesignplayground.urlshortener.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
class V1UrlShortenerApiController {

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
}
