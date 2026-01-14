package com.example.service;

import com.example.domain.Event;
import com.example.domain.EventTicket;
import com.example.domain.enums.EventType;
import com.example.exception.ForbiddenActionException;
import com.example.exception.ResourceNotFoundException;
import com.example.persistence.repository.EventRepository;
import com.example.persistence.repository.EventTicketRepository;
import com.example.web.dto.filter.EventFilter;
import com.example.security.RoleSecurity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service handling business logic for Events.
 */
@ApplicationScoped
public class EventService {

    private final ConnectedApplicationUserService connectedApplicationUserService;
    private final EventRepository eventRepository;
    private final EventTicketRepository eventTicketRepository;
    private final RoleSecurity roleSecurity;

    public EventService(ConnectedApplicationUserService connectedApplicationUserService, EventRepository eventRepository, RoleSecurity roleSecurity, EventTicketRepository eventTicketRepository) {
        this.connectedApplicationUserService = connectedApplicationUserService;
        this.eventRepository = eventRepository;
        this.roleSecurity = roleSecurity;
        this.eventTicketRepository = eventTicketRepository;
    }

    public Event getById(Long id) {
        Optional<Event> eventOpt = this.eventRepository.getById(id);
        return eventOpt.orElseThrow(
                () -> new ResourceNotFoundException("Event with ID: " + id + " does not exist")
        );
    }

    public List<Event> getAll(EventFilter eventFilter) {
        if (!this.roleSecurity.canViewEventType(eventFilter.getEventType())) {
            throw new ForbiddenActionException("View event type " + eventFilter.getEventType() + " forbidden");
        }
        return this.eventRepository.getAll(eventFilter);
    }

    @Transactional
    public Event create(String title, String description, EventType eventType) {
        if (!this.roleSecurity.canCreateOrEditEvents(eventType)) {
            throw new ForbiddenActionException("Create event type " + eventType + " forbidden");
        }
        Event event = new Event(title, description, eventType, this.connectedApplicationUserService.getApplicationUser());
        this.eventRepository.create(event);
        return event;
    }

    @Transactional
    public Event update(Long id, String title, String description, Boolean archive) {

        Event eventToUpdate = this.getById(id);

        this.canUpdateCheck(eventToUpdate);

        // If archive selected (and the event is not already archived), set the archive field to now
        if (archive != null && archive && eventToUpdate.getArchivedAt() == null) {
            Instant now = Instant.now();
            eventToUpdate.setArchivedAt(now);
        }
        eventToUpdate.setLastUpdatedBy(this.connectedApplicationUserService.getApplicationUser());
        eventToUpdate.setTitle(title);
        eventToUpdate.setDescription(description);
        return this.eventRepository.update(eventToUpdate);

    }


    public List<EventTicket> getTicketsForEvent(Long id) {
        Event event = this.getById(id);

        if (!this.roleSecurity.canViewEventType(event.getEventType())) {
            throw new ForbiddenActionException("View tickets with parent event type " + event.getEventType() + " forbidden");
        }
        return this.eventTicketRepository.getAllForEvent(event);

    }


    public void canUpdateCheck(Event eventToUpdate) {
        if (eventToUpdate.getArchivedAt() != null) {
            throw new ForbiddenActionException("Cannot update archived event " + eventToUpdate.getId());
        }
        if (!this.roleSecurity.canCreateOrEditEvents(eventToUpdate.getEventType())) {
            throw new ForbiddenActionException("Update event type " + eventToUpdate.getEventType() + " forbidden");
        }
    }
}
