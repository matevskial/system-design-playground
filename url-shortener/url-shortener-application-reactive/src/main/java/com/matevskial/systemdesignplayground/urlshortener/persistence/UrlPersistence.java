package com.matevskial.systemdesignplayground.urlshortener.persistence;

import reactor.core.publisher.Mono;

public interface UrlPersistence {

    Mono<String> findShortened(String originalUrl);

    Mono<Void> saveShortened(String originalUrl, String shortened);

    Mono<String> findOriginalUrl(String shortened);
}
