package com.dogial.user.persistence.dao;

import com.dogial.user.persistence.entities.UserEntity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
@Slf4j
public class UserDao {

    private final EntityManager entityManager;

    @Inject
    public UserDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public Optional<UserEntity> findByEmail(String email) {
        final Query query = entityManager.createQuery(
                        "SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class)
                .setParameter("email", email);
        return query.getResultList().stream().findFirst();
    }

    @Transactional
    public boolean existsByEmail(String email) {
        final Query query = entityManager.createQuery(
                        "SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email")
                .setParameter("email", email);
        return ((Long) query.getSingleResult()) > 0;
    }

    @Transactional
    public Optional<UserEntity> findByEmailAndPasswordHash(String email, String passwordHash) {
        final Query query = entityManager.createQuery(
                        "SELECT u FROM UserEntity u WHERE u.email = :email AND u.passwordHash = :passwordHash",
                        UserEntity.class)
                .setParameter("email", email)
                .setParameter("passwordHash", passwordHash);
        return query.getResultList().stream().findFirst();
    }

    @Transactional
    public long countByCreatedAtBetween(Instant startDate, Instant endDate) {
        final Query query = entityManager.createQuery(
                        "SELECT COUNT(u) FROM UserEntity u WHERE u.createdAt BETWEEN :startDate AND :endDate")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
        return (Long) query.getSingleResult();
    }

    @Transactional
    public List<UserEntity> findByCreatedAtBetween(Instant startDate, Instant endDate) {
        final Query query = entityManager.createQuery(
                        "SELECT u FROM UserEntity u WHERE u.createdAt BETWEEN :startDate AND :endDate",
                        UserEntity.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
        return query.getResultList();
    }

    @Transactional
    public UserEntity save(UserEntity entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Transactional
    public UserEntity update(UserEntity entity) {
        return entityManager.merge(entity);
    }

    @Transactional
    public void delete(UserEntity entity) {
        entityManager.remove(entity);
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional
    public Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(UserEntity.class, id));
    }

    @Transactional
    public boolean existsById(UUID id) {
        final Query query = entityManager.createQuery(
                        "SELECT COUNT(u) FROM UserEntity u WHERE u.id = :id")
                .setParameter("id", id);
        return ((Long) query.getSingleResult()) > 0;
    }
}