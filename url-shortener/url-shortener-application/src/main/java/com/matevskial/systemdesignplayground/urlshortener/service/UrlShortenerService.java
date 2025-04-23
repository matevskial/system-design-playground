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
        String shortened;
        if (shortenedOptional.isPresent()) {
            shortened = shortenedOptional.get();
        } else {
            shortened = urlShortener.shorten(originalUrl);
            urlPersistence.saveShortened(originalUrl, shortened);
        }

        return String.format("%s/%s", urlShortenerProperties.getBaseUrl(), shortened);
    }

    public Optional<String> getOriginalUrl(String shortened) {
        return urlPersistence.findOriginalUrl(shortened);
    }
}
