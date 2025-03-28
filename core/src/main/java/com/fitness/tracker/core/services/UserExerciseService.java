package com.fitness.tracker.core.services;

import com.fitness.tracker.data.models.UserExercise;
import com.fitness.tracker.data.models.AppUser;
import com.fitness.tracker.data.models.Exercise;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class UserExerciseService {
    private final EntityManager entityManager;

    public UserExerciseService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public UserExercise create(UserExercise userExercise) {
        entityManager.getTransaction().begin();
        entityManager.persist(userExercise);
        entityManager.getTransaction().commit();
        return userExercise;
    }

    public UserExercise edit(UserExercise userExercise) {
        entityManager.getTransaction().begin();
        UserExercise updatedUserExercise = entityManager.merge(userExercise);
        entityManager.getTransaction().commit();
        return updatedUserExercise;
    }

    public void delete(Long id) {
        entityManager.getTransaction().begin();
        UserExercise userExercise = entityManager.find(UserExercise.class, id);
        if (userExercise != null) {
            entityManager.remove(userExercise);
        }
        entityManager.getTransaction().commit();
    }

    public UserExercise findById(Long id) {
        return entityManager.find(UserExercise.class, id);
    }

    public List<UserExercise> findByUser(AppUser user) {
        TypedQuery<UserExercise> query = entityManager.createQuery(
            "SELECT ue FROM UserExercise ue WHERE ue.user = :user", UserExercise.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public List<UserExercise> findByExercise(Exercise exercise) {
        TypedQuery<UserExercise> query = entityManager.createQuery(
            "SELECT ue FROM UserExercise ue WHERE ue.exercise = :exercise", UserExercise.class);
        query.setParameter("exercise", exercise);
        return query.getResultList();
    }

    public List<UserExercise> findActiveGoals(AppUser user) {
        TypedQuery<UserExercise> query = entityManager.createQuery(
            "SELECT ue FROM UserExercise ue WHERE ue.user = :user " +
            "AND (ue.goalDeadline IS NULL OR ue.goalDeadline >= CURRENT_DATE)",
            UserExercise.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public List<UserExercise> findExpiredGoals(AppUser user) {
        TypedQuery<UserExercise> query = entityManager.createQuery(
            "SELECT ue FROM UserExercise ue WHERE ue.user = :user " +
            "AND ue.goalDeadline < CURRENT_DATE",
            UserExercise.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public UserExercise findUserExercise(AppUser user, Exercise exercise) {
        TypedQuery<UserExercise> query = entityManager.createQuery(
            "SELECT ue FROM UserExercise ue WHERE ue.user = :user " +
            "AND ue.exercise = :exercise", UserExercise.class);
        query.setParameter("user", user);
        query.setParameter("exercise", exercise);
        try {
            return query.getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    public void delete(UserExercise userExercise) {
        if (userExercise != null) {
            delete(userExercise.getId());
        }
    }
} 