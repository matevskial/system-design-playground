package com.matevskial.systemdesignplayground.urlshortener.persistence.r2dbc;

import com.matevskial.systemdesignplayground.urlshortener.persistence.UrlPersistence;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface R2dbcUrlPersistence extends UrlPersistence, R2dbcRepository<UrlEntity, Long> {

    @Override
    default Mono<String> findShortened(String originalUrl) {
        return findByOriginalUrl(originalUrl).map(UrlEntity::getShortened);
    }

    Mono<UrlEntity> findByOriginalUrl(String originalUrl);

    @Override
    default Mono<Void> saveShortened(String originalUrl, String shortened) {
        return Mono.fromSupplier(() -> UrlEntity.newEntity(originalUrl, shortened)).flatMap(this::save).then();
    }

    @Override
    default Mono<String> findOriginalUrl(String shortened) {
        return findByShortened(shortened).map(UrlEntity::getOriginalUrl);
    }

    Mono<UrlEntity> findByShortened(String shortened);
}
