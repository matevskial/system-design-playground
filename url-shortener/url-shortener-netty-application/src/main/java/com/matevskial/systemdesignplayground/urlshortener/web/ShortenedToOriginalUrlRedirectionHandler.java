package com.matevskial.systemdesignplayground.urlshortener.web;

import com.matevskial.systemdesignplayground.urlshortener.framework.web.*;
import com.matevskial.systemdesignplayground.urlshortener.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class ShortenedToOriginalUrlRedirectionHandler implements RequestHandler {

    private final UrlShortenerService urlShortenerService;

    @Override
    public void setupHandlers(RequestHandlers requestHandlers) {
        requestHandlers
                .path("/{shortened}")
                .method(HttpMethod.GET)
                .register(this::redirectToOriginalUrl);
    }

    public void redirectToOriginalUrl(Request request, Response response) {
        try {
            String shortened = request.getPathVariable("shortened");
            Optional<String> originalUrl = urlShortenerService.getOriginalUrl(shortened);
            if (originalUrl.isPresent()) {
                response.setLocationHeaderValue(originalUrl.get());
                response.setStatus(302);
            } else {
                response.setStatus(404);
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.setText("Unexpected error while redirecting to original URL");
        }
    }
}
