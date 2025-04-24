package com.matevskial.systemdesignplayground.urlshortener.service;

import com.matevskial.systemdesignplayground.urlshortener.config.UrlShortenerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

//    private final UrlPersistence urlPersistence;
//    private final UrlShortener urlShortener;
    private final UrlShortenerProperties urlShortenerProperties;

    public Mono<String> getOrCreateShortenedUrl(String originalUrl) {
//        var shortenedOptional = urlPersistence.findShortened(originalUrl);
//        String shortened;
//        if (shortenedOptional.isPresent()) {
//            shortened = shortenedOptional.get();
//        } else {
//            shortened = urlShortener.shorten(originalUrl);
//            urlPersistence.saveShortened(originalUrl, shortened);
//        }
//
//        return String.format("%s/%s", urlShortenerProperties.getBaseUrl(), shortened);
        return Mono.just(String.format("%s/%s", urlShortenerProperties.getBaseUrl(), "abcd"));
    }

    public Mono<Optional<String>> getOriginalUrl(String shortened) {
        return Mono.just(Optional.of("https://x.com"));
//        return urlPersistence.findOriginalUrl(shortened);
    }
}
