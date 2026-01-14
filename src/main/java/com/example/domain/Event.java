package com.example.domain;

import com.example.domain.enums.EventType;
import com.example.persistence.listener.AuditableEntityListener;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@EntityListeners(AuditableEntityListener.class)
@Table(name = "EVENT")
public class Event implements AuditableEntity {

    public Event() {
    }

    public Event(String title, String description, EventType eventType, ApplicationUser createdBy) {
        this.title = title;
        this.description = description;
        this.eventType = eventType;
        this.createdBy = createdBy;
        this.lastUpdatedBy = createdBy;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITLE", length = 128)
    private String title;

    @Column(name = "DESCRIPTION", length = 1024)
    private String description;

    @Column(name = "EVENT_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @Column(name = "LAST_UPDATED_AT", nullable = false)
    private Instant lastUpdatedAt;

    @Column(name = "ARCHIVED_AT")
    private Instant archivedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "CREATED_BY_ID", nullable = false)
    private ApplicationUser createdBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "LAST_UPDATED_BY_ID", nullable = false)
    private ApplicationUser lastUpdatedBy;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType type) {
        this.eventType = type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(Instant archivedAt) {
        this.archivedAt = archivedAt;
    }

    public ApplicationUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ApplicationUser createdBy) {
        this.createdBy = createdBy;
    }

    public ApplicationUser getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(ApplicationUser updatedBy) {
        this.lastUpdatedBy = updatedBy;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Instant updatedAt) {
        this.lastUpdatedAt = updatedAt;
    }
}
