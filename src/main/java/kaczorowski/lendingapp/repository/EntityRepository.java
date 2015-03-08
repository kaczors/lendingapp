package kaczorowski.lendingapp.repository;

import org.springframework.core.GenericTypeResolver;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

abstract class EntityRepository<T> {

    @PersistenceContext
    EntityManager entityManager;

    private final Class<T> clazz;

    protected EntityRepository() {
        clazz = (Class<T>) GenericTypeResolver.resolveTypeArguments(getClass(), EntityRepository.class)[0];
    }

    public T save(T entity) {
        return entityManager.merge(entity);
    }

    public T load(Long id) {
        T entity = entityManager.find(clazz, id);
        if (entity == null) {
            throw new NoResultException(clazz.getSimpleName() + " with id " + id + " does not exits.");
        }
        return entity;
    }
}
