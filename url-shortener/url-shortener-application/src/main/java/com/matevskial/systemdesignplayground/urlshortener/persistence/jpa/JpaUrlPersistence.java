package com.matevskial.systemdesignplayground.urlshortener.persistence.jpa;

import com.matevskial.systemdesignplayground.urlshortener.persistence.UrlPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUrlPersistence extends UrlPersistence, JpaRepository<UrlEntity, Long> {

    @Override
    default Optional<String> findShortened(String originalUrl) {
        return findByOriginalUrl(originalUrl).map(UrlEntity::getShortened);
    }

    Optional<UrlEntity> findByOriginalUrl(String originalUrl);

    @Override
    default void saveShortened(String url, String shortened) {
        UrlEntity urlEntity = UrlEntity.of(1L, url, shortened);
        save(urlEntity);
    }

    @Override
    default Optional<String> findOriginalUrl(String shortened) {
        return findByShortened(shortened).map(UrlEntity::getOriginalUrl);
    }

    Optional<UrlEntity> findByShortened(String shortened);
}
