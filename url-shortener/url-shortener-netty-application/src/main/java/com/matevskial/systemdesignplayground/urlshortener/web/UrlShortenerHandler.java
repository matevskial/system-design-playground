package com.matevskial.systemdesignplayground.urlshortener.web;

import com.matevskial.systemdesignplayground.urlshortener.framework.web.*;
import com.matevskial.systemdesignplayground.urlshortener.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UrlShortenerHandler implements RequestHandler {

    private final UrlShortenerService urlShortenerService;

    @Override
    public void setupHandlers(RequestHandlers requestHandlers) {
        requestHandlers
                .register()
                .path("/api/v1/shorten")
                .method(HttpMethod.POST)
                .handler(this::shorten);
    }

    public void shorten(Request request, Response response) {
        try {
            QueryParameterValue originalUrl = request.getQueryParameters().getSingleValue("originalUrl");
            if (originalUrl == QueryParameterValue.STRING_VALUE) {
                String shortenedUrl = urlShortenerService.getOrCreateShortenedUrl(originalUrl.getStringValue());
                response.setText(shortenedUrl);
                response.setLocationHeaderValue(shortenedUrl);
                response.setStatus(201);
            }
        } catch (Exception e) {
            response.setText("Unexpected error while shortening URL");
            response.setStatus(500);
        }
    }
}
