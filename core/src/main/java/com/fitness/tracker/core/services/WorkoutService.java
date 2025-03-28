package com.fitness.tracker.core.services;

import com.fitness.tracker.data.models.Workout;
import com.fitness.tracker.data.models.AppUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class WorkoutService {
    private final EntityManager entityManager;

    public WorkoutService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Workout create(Workout workout) {
        entityManager.getTransaction().begin();
        entityManager.persist(workout);
        entityManager.getTransaction().commit();
        return workout;
    }

    public Workout edit(Workout workout) {
        entityManager.getTransaction().begin();
        Workout updatedWorkout = entityManager.merge(workout);
        entityManager.getTransaction().commit();
        return updatedWorkout;
    }

    public void delete(Long id) {
        entityManager.getTransaction().begin();
        Workout workout = entityManager.find(Workout.class, id);
        if (workout != null) {
            entityManager.remove(workout);
        }
        entityManager.getTransaction().commit();
    }

    public Workout findById(Long id) {
        return entityManager.find(Workout.class, id);
    }

    public List<Workout> findByUser(AppUser user) {
        TypedQuery<Workout> query = entityManager.createQuery(
            "SELECT w FROM Workout w WHERE w.user = :user", Workout.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public List<Workout> findByName(String name) {
        TypedQuery<Workout> query = entityManager.createQuery(
            "SELECT w FROM Workout w WHERE LOWER(w.name) LIKE LOWER(:name)", Workout.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    public void delete(Workout workout) {
        if (workout != null) {
            delete(workout.getId());
        }
    }
} 