package com.example.web.dto.response;

import com.example.domain.ApplicationUser;
import com.example.domain.Event;
import com.example.domain.enums.EventType;

import java.time.Instant;

public class EventDto {

    public final Long id;
    public final String title;
    public final String description;
    public final EventType eventType;
    public final Instant createdAt;
    public final Instant updatedAt;
    public final Instant archivedAt;
    public final ApplicationUserDto createdBy;
    public final ApplicationUserDto lastUpdatedBy;

    public EventDto(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.eventType = event.getEventType();
        this.createdAt = event.getCreatedAt();
        this.archivedAt = event.getArchivedAt();
        this.updatedAt = event.getLastUpdatedAt();
        this.createdBy = new ApplicationUserDto(event.getCreatedBy());
        this.lastUpdatedBy = new ApplicationUserDto(event.getLastUpdatedBy());
    }
}
