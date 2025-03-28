package com.fitness.tracker.core.services;

import com.fitness.tracker.data.models.AppUser;
import com.fitness.tracker.data.models.TrainingSession;
import com.fitness.tracker.data.models.Workout;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

public class TrainingSessionService {
    private final EntityManager entityManager;

    public TrainingSessionService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TrainingSession create(TrainingSession session) {
        entityManager.getTransaction().begin();
        entityManager.persist(session);
        entityManager.getTransaction().commit();
        return session;
    }

    public TrainingSession edit(TrainingSession session) {
        entityManager.getTransaction().begin();
        TrainingSession updatedSession = entityManager.merge(session);
        entityManager.getTransaction().commit();
        return updatedSession;
    }

    public void delete(Long id) {
        entityManager.getTransaction().begin();
        TrainingSession session = entityManager.find(TrainingSession.class, id);
        if (session != null) {
            entityManager.remove(session);
        }
        entityManager.getTransaction().commit();
    }

    public TrainingSession findById(Long id) {
        return entityManager.find(TrainingSession.class, id);
    }

    public List<TrainingSession> findByWorkout(Workout workout) {
        TypedQuery<TrainingSession> query = entityManager.createQuery(
                "SELECT ts FROM TrainingSession ts WHERE ts.workout = :workout", TrainingSession.class);
        query.setParameter("workout", workout);
        return query.getResultList();
    }

    public List<TrainingSession> findByDateRange(LocalDate startDate, LocalDate endDate) {
        TypedQuery<TrainingSession> query = entityManager.createQuery(
                "SELECT ts FROM TrainingSession ts WHERE ts.date BETWEEN :startDate AND :endDate",
                TrainingSession.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    public List<TrainingSession> findByUser(AppUser user) {
        TypedQuery<TrainingSession> query = entityManager.createQuery(
                "SELECT ts FROM TrainingSession ts WHERE ts.workout.user = :user",
                TrainingSession.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public List<TrainingSession> findRecentSessions(AppUser user, int limit) {
        TypedQuery<TrainingSession> query = entityManager.createQuery(
                "SELECT ts FROM TrainingSession ts WHERE ts.workout.user = :user " +
                        "ORDER BY ts.date DESC", TrainingSession.class);
        query.setParameter("user", user);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public long getSessionCount(AppUser user) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(ts) FROM TrainingSession ts WHERE ts.workout.user = :user",
                Long.class);
        query.setParameter("user", user);
        return query.getSingleResult();
    }

    public TrainingSession save(TrainingSession session) {
        if (session.getId() == null) {
            return create(session);
        } else {
            return edit(session);
        }
    }

    public List<TrainingSession> getUserSessions(AppUser user) {
        TypedQuery<TrainingSession> query = entityManager.createQuery(
                "SELECT ts FROM TrainingSession ts WHERE ts.workout.user = :user " +
                        "ORDER BY ts.date DESC", TrainingSession.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public void delete(TrainingSession session) {
        if (session != null) {
            delete(session.getId());
        }
    }

    public List<TrainingSession> getAllSessions() {
        TypedQuery<TrainingSession> query = entityManager.createQuery(
                "SELECT ts FROM TrainingSession ts ORDER BY ts.date", TrainingSession.class);
        return query.getResultList();
    }
}