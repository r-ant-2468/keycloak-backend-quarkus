package com.example.domain;

import java.time.Instant;

public interface AuditableEntity {
    void setCreatedAt(Instant createdAt);

    void setLastUpdatedAt(Instant createdAt);
}
