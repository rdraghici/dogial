package com.dogial.dog.persistence.dao;

import com.dogial.dog.persistence.entities.DogEntity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Singleton
@Slf4j
public class DogDao {

    private final EntityManager entityManager;

    @Inject
    public DogDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public Optional<DogEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(DogEntity.class, id));
    }

    @Transactional
    public DogEntity save(DogEntity entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Transactional
    public DogEntity update(DogEntity entity) {
        return entityManager.merge(entity);
    }

    @Transactional
    public void delete(DogEntity entity) {
        entityManager.remove(entity);
        entityManager.flush();
        entityManager.clear();
    }
}