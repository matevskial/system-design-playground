package com.matevskial.systemdesignplayground.urlshortener.core;

import java.net.URI;

public record UrlShortenerProperties(
        String baseUrl
) {
    public UrlShortenerProperties {
        try {
            URI.create(baseUrl).toURL();
        } catch (Exception e) {
            throw new IllegalArgumentException("baseUrl is invalid URL: " + baseUrl);
        }
    }
}
