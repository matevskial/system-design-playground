package com.matevskial.systemdesignplayground.urlshortener.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.matevskial.systemdesignplayground.urlshortener.service.UrlShortenerService;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
class V1UrlShortenerApiController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping("/shorten")
    public Mono<ResponseEntity<String>> shorten(@RequestPart("originalUrl") Mono<FormFieldPart> originalUrlMono) {
        return originalUrlMono.flatMap(originalUrlPart -> {
            String originalUrl = originalUrlPart.value();
            return urlShortenerService.getOrCreateShortenedUrl(originalUrl).map(shortenedUrl -> {
                return ResponseEntity.created(URI.create(shortenedUrl)).body(shortenedUrl);
            }).onErrorResume(exception -> Mono.fromSupplier(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error while shortening URL")));
        });
    }
}
