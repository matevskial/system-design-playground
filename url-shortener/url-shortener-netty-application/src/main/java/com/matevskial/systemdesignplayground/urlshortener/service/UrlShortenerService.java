package com.matevskial.systemdesignplayground.urlshortener.service;

import com.matevskial.systemdesignplayground.urlshortener.framework.transaction.Transaction;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShortener;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShortenerProperties;
import com.matevskial.systemdesignplayground.urlshortener.persistence.UrlPersistence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class UrlShortenerService {

    private final UrlPersistence urlPersistence;
    private final UrlShortener urlShortener;
    private final UrlShortenerProperties urlShortenerProperties;

    public String getOrCreateShortenedUrl(String originalUrl) {
        return Transaction.withTransaction(() -> {
            log.info("Shortening URL {}", originalUrl);
            var shortenedOptional = urlPersistence.findShortened(originalUrl);
            String shortened;
            if (shortenedOptional.isPresent()) {
                shortened = shortenedOptional.get();
            } else {
                shortened = urlShortener.shorten(originalUrl);
                urlPersistence.saveShortened(originalUrl, shortened);
            }

            return String.format("%s/%s", urlShortenerProperties.baseUrl(), shortened);
        });
    }

    public Optional<String> getOriginalUrl(String shortened) {
        return urlPersistence.findOriginalUrl(shortened);
    }
}
