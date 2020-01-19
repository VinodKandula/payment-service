package com.payment.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.function.Function;

/**
 * @author Vinod Kandula
 */
public abstract class BaseDaoRepository {

    private EntityManagerFactory emFactory;

    protected BaseDaoRepository(EntityManagerFactory factory) {
        this.emFactory = factory;
    }

    protected <T> T executeInTransaction(Function<EntityManager, T> function) {
        EntityManager entityManager = emFactory.createEntityManager();
        entityManager.getTransaction().begin();

        T value;
        try {
            value = function.apply(entityManager);
            entityManager.getTransaction().commit();
        } catch (Throwable e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }

        return value;
    }

    protected <T> T executeQuery(Function<EntityManager, T> function) {
        EntityManager entityManager = emFactory.createEntityManager();

        try {
           return function.apply(entityManager);
        } finally {
            entityManager.close();
        }
    }
}
