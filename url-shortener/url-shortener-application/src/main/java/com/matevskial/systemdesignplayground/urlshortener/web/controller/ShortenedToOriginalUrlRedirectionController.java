package com.matevskial.systemdesignplayground.urlshortener.web.controller;

import com.matevskial.systemdesignplayground.urlshortener.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/{shortened}")
@RequiredArgsConstructor
class ShortenedToOriginalUrlRedirectionController {

    private final UrlShortenerService urlShortenerService;

    @GetMapping
    public ResponseEntity<Object> redirectToOriginalUrl(@PathVariable String shortened) {
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
