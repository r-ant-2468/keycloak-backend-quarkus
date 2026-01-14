package com.example.persistence.repository;

import com.example.domain.ApplicationUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

@ApplicationScoped
public class ApplicationUserRepository {

    private final EntityManager entityManager;

    public ApplicationUserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<ApplicationUser> getById(String id) {
        TypedQuery<ApplicationUser> query = this.entityManager.createQuery("FROM ApplicationUser WHERE id = :id", ApplicationUser.class);
        query.setParameter("id", id);
        return Optional.ofNullable(query.getSingleResultOrNull());
    }

    public void create(ApplicationUser applicationUserToCreate) {
        this.entityManager.persist(applicationUserToCreate);
    }

    public ApplicationUser update(ApplicationUser applicationUserToCreate) {
        return this.entityManager.merge(applicationUserToCreate);
    }

}
