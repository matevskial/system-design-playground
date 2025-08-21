package com.matevskial.systemdesignplayground.urlshortener.hibernate;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HibernateSessionManager {

    private static SessionFactory sessionFactory;

    /**
     * Not thread safe, do not call this method in 2 or more threads that might execute
     * in an interleaving manner. This is designed to be called in the initialization part of the
     * application
     */
    public static void initializeSessionManager() throws HibernateSessionManagerException {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new HibernateSessionManagerException(ex);
        }
    }

    public static EntityManager openEntityManager() throws HibernateSessionManagerException {
        try {
            return sessionFactory.openSession();
        } catch (Throwable ex) {
            throw new HibernateSessionManagerException(ex);
        }
    }
}
