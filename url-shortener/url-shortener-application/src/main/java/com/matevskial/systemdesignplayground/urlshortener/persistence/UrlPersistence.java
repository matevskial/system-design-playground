package com.matevskial.systemdesignplayground.urlshortener.persistence;

import java.util.Optional;

public interface UrlPersistence {

    Optional<String> findShortened(String url);

    void saveShortened(String url, String shortened);

    Optional<String> findOriginalUrl(String shortened);
}
