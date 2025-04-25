package com.matevskial.systemdesignplayground.urlshortener.service;

import com.matevskial.systemdesignplayground.urlshortener.config.UrlShortenerProperties;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShortener;
import com.matevskial.systemdesignplayground.urlshortener.persistence.UrlPersistence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private final UrlPersistence urlPersistence;
    private final UrlShortener urlShortener;
    private final UrlShortenerProperties urlShortenerProperties;

    public Mono<String> getOrCreateShortenedUrl(String originalUrl) {
        return urlPersistence.findShortened(originalUrl).switchIfEmpty(Mono.defer(() -> {
            String shortened = urlShortener.shorten(originalUrl);
            return urlPersistence.saveShortened(originalUrl, shortened).thenReturn(shortened);
        })).map(shortened -> {
            return String.format("%s/%s", urlShortenerProperties.getBaseUrl(), shortened);
        });
    }

    public Mono<String> getOriginalUrl(String shortened) {
        return urlPersistence.findOriginalUrl(shortened);
    }
}
