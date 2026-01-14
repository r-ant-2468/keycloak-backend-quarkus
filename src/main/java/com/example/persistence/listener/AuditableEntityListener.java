package com.example.persistence.listener;

import com.example.domain.ApplicationUser;
import com.example.domain.AuditableEntity;
import com.example.service.ApplicationUserService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.Instant;

@Dependent
public class AuditableEntityListener {

    @PrePersist
    public void prePersist(Object entityToPersist) {
        if (entityToPersist instanceof AuditableEntity auditableEntity) {
            Instant now = Instant.now();
            auditableEntity.setCreatedAt(now);
            auditableEntity.setLastUpdatedAt(now);

        }
    }

    @PreUpdate
    public void preUpdate(Object entityToUpdate) {
        if (entityToUpdate instanceof AuditableEntity auditableEntity) {
            Instant now = Instant.now();
            auditableEntity.setLastUpdatedAt(now);
        }
    }
}
