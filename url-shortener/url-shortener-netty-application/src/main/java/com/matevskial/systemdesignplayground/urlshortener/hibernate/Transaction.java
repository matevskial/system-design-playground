package com.matevskial.systemdesignplayground.urlshortener.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Transaction {

    public static <R> R withTransaction(EntityManagerFunction<R> function) {
        EntityManager entityManager = HibernateSessionManager.openEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            R result = function.apply(entityManager);
            transaction.commit();
            return result;
        } catch (Throwable e) {
            transaction.rollback();
            throw e;
        }
    }

    @FunctionalInterface
    public interface EntityManagerFunction<R> extends Function<EntityManager, R> {

    }
}
