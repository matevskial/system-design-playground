package com.matevskial.systemdesignplayground.urlshortener.persistence.hibernate;

import com.matevskial.systemdesignplayground.urlshortener.hibernate.Transaction;
import com.matevskial.systemdesignplayground.urlshortener.persistence.UrlPersistence;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class UrlPersistenceImpl implements UrlPersistence {

    @Override
    public Optional<String> findShortened(String url) {
        return Transaction.withTransaction(entityManager -> {
            TypedQuery<String> query = entityManager.createQuery("select u.shortened from UrlEntity u where u.originalUrl = :url", String.class);
            query.setParameter("url", url);
            try {
                return Optional.ofNullable(query.getSingleResult());
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public void saveShortened(String url, String shortened) {
        Transaction.withTransaction(entityManager -> {
            UrlEntity urlEntity = UrlEntity.newEntity(url, shortened);
            entityManager.persist(urlEntity);
            return null;
        });
    }

    @Override
    public Optional<String> findOriginalUrl(String shortened) {
        return Transaction.withTransaction(entityManager -> {
            TypedQuery<String> query = entityManager.createQuery("select u.originalUrl from UrlEntity u where u.shortened = :shortened", String.class);
            query.setParameter("shortened", shortened);
            try {
                return Optional.ofNullable(query.getSingleResult());
            } catch (NoResultException e) {
                return Optional.empty();
            }
        });
    }
}
