package com.matevskial.systemdesignplayground.urlshortener.service;

import com.matevskial.systemdesignplayground.urlshortener.config.UrlShortenerProperties;
import com.matevskial.systemdesignplayground.urlshortener.core.UrlShortener;
import com.matevskial.systemdesignplayground.urlshortener.persistence.UrlPersistence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private final UrlPersistence urlPersistence;
    private final UrlShortener urlShortener;
    private final UrlShortenerProperties urlShortenerProperties;

    public String getOrCreateShortenedUrl(String originalUrl) {
        var shortenedOptional = urlPersistence.findShortened(originalUrl);
        if (shortenedOptional.isPresent()) {
            return shortenedOptional.get();
        }

        String shortened = urlShortener.shorten(originalUrl);
        urlPersistence.saveShortened(originalUrl, shortened);
        return String.format("%s/%s", urlShortenerProperties.getBaseUrl().toString(), shortened);
    }

    public Optional<String> getOriginalUrl(String shortened) {
        return urlPersistence.findOriginalUrl(shortened);
    }
}
