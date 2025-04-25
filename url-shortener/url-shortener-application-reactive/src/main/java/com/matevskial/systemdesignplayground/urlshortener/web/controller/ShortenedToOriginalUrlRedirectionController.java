package com.matevskial.systemdesignplayground.urlshortener.web.controller;

import com.matevskial.systemdesignplayground.urlshortener.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/{shortened}")
@RequiredArgsConstructor
class ShortenedToOriginalUrlRedirectionController {

    private final UrlShortenerService urlShortenerService;

    @GetMapping
    public Mono<ResponseEntity<Object>> redirectToOriginalUrl(@PathVariable String shortened) {
        return urlShortenerService.getOriginalUrl(shortened).map(originalUrl -> {
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
        }).switchIfEmpty(Mono.fromSupplier(() -> {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        })).onErrorResume(exception -> {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error while redirecting to original URL"));
        });
    }
}
