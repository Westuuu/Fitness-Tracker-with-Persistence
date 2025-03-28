package com.fitness.tracker.core.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ServiceFactory {
    private static ServiceFactory instance;
    private static EntityManagerFactory emf;
    private static EntityManager em;
    
    public static void init() {
        if (instance == null) {
            instance = new ServiceFactory();
            emf = Persistence.createEntityManagerFactory("fitness_tracker");
            em = emf.createEntityManager();
        }
    }
    
    public static UserService createUserService() {
        return new UserService(em);
    }
    
    public static ExerciseService createExerciseService() {
        return new ExerciseService(em);
    }
    
    public static WorkoutService createWorkoutService() {
        return new WorkoutService(em);
    }
    
    public static UserExerciseService createUserExerciseService() {
        return new UserExerciseService(em);
    }
    
    public static TrainingSessionService createTrainingSessionService() {
        return new TrainingSessionService(em);
    }
    
    public static void cleanup() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.createEntityManager();
        }
    }
} 