package com.fitness.tracker.core.services;

import com.fitness.tracker.data.models.Exercise;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ExerciseService {
    private final EntityManager entityManager;

    public ExerciseService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Exercise create(Exercise exercise) {
        entityManager.getTransaction().begin();
        entityManager.persist(exercise);
        entityManager.getTransaction().commit();
        return exercise;
    }

    public Exercise edit(Exercise exercise) {
        entityManager.getTransaction().begin();
        Exercise updatedExercise = entityManager.merge(exercise);
        entityManager.getTransaction().commit();
        return updatedExercise;
    }

    public void delete(Long id) {
        entityManager.getTransaction().begin();
        Exercise exercise = entityManager.find(Exercise.class, id);
        if (exercise != null) {
            entityManager.remove(exercise);
        }
        entityManager.getTransaction().commit();
    }

    public Exercise findById(Long id) {
        return entityManager.find(Exercise.class, id);
    }

    public List<Exercise> findByType(String type) {
        TypedQuery<Exercise> query = entityManager.createQuery(
            "SELECT e FROM Exercise e WHERE e.type = :type", Exercise.class);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<Exercise> findByName(String name) {
        TypedQuery<Exercise> query = entityManager.createQuery(
            "SELECT e FROM Exercise e WHERE LOWER(e.name) LIKE LOWER(:name)", Exercise.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    public List<Exercise> findAll() {
        TypedQuery<Exercise> query = entityManager.createQuery(
            "SELECT e FROM Exercise e", Exercise.class);
        return query.getResultList();
    }

    public List<String> getAllExerciseTypes() {
        TypedQuery<String> query = entityManager.createQuery(
            "SELECT DISTINCT e.type FROM Exercise e", String.class);
        return query.getResultList();
    }

    public boolean exists(String name) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(e) FROM Exercise e WHERE LOWER(e.name) = LOWER(:name)", Long.class);
        query.setParameter("name", name);
        return query.getSingleResult() > 0;
    }

    public Exercise save(Exercise exercise) {
        if (exercise.getId() == null) {
            return create(exercise);
        } else {
            return edit(exercise);
        }
    }

    public List<Exercise> getAllExercises() {
        return findAll();
    }
} 