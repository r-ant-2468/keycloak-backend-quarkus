package com.example.web.dto.filter;

import com.example.domain.enums.EventType;
import jakarta.ws.rs.QueryParam;

public class EventFilter {

    @QueryParam("eventType")
    public EventType eventType;

    @QueryParam("archived")
    public Boolean archived;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
}
