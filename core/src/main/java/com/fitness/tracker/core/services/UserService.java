package com.fitness.tracker.core.services;

import com.fitness.tracker.data.models.AppUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class UserService {
    private final EntityManager entityManager;

    public UserService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public AppUser create(AppUser user) {
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        return user;
    }

    public AppUser edit(AppUser user) {
        entityManager.getTransaction().begin();
        AppUser updatedUser = entityManager.merge(user);
        entityManager.getTransaction().commit();
        return updatedUser;
    }

    public void delete(Long id) {
        entityManager.getTransaction().begin();
        AppUser user = entityManager.find(AppUser.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
        entityManager.getTransaction().commit();
    }

    public AppUser findById(Long id) {
        return entityManager.find(AppUser.class, id);
    }

    public AppUser findByEmail(String email) {
        TypedQuery<AppUser> query = entityManager.createQuery(
            "SELECT u FROM AppUser u WHERE u.email = :email", AppUser.class);
        query.setParameter("email", email);
        return query.getSingleResult();
    }

    public List<AppUser> findByName(String name) {
        TypedQuery<AppUser> query = entityManager.createQuery(
            "SELECT u FROM AppUser u WHERE LOWER(u.name) LIKE LOWER(:name)", AppUser.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    public AppUser register(String name, String email) {
        try {
            findByEmail(email);
            throw new IllegalArgumentException("User with this email already exists");
        } catch (jakarta.persistence.NoResultException e) {
            AppUser newUser = new AppUser();
            newUser.setName(name);
            newUser.setEmail(email);
            return create(newUser);
        }
    }

    public AppUser authenticate(String email) {
        try {
            return findByEmail(email);
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    public List<AppUser> findAll() {
        TypedQuery<AppUser> query = entityManager.createQuery(
            "SELECT u FROM AppUser u", AppUser.class);
        return query.getResultList();
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }
} 