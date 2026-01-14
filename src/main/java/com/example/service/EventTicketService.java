package com.example.service;

import com.example.domain.Event;
import com.example.domain.EventTicket;
import com.example.exception.ForbiddenActionException;
import com.example.exception.ResourceNotFoundException;
import com.example.persistence.repository.EventTicketRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;

/**
 * Service class responsible for handling operations related to event tickets.
 */
@ApplicationScoped
public class EventTicketService {

    private final ConnectedApplicationUserService connectedApplicationUserService;
    private final EventTicketRepository eventTicketRepository;
    private final EventService eventService;

    public EventTicketService(EventTicketRepository eventTicketRepository, EventService eventService, ConnectedApplicationUserService connectedApplicationUserService) {
        this.eventTicketRepository = eventTicketRepository;
        this.eventService = eventService;
        this.connectedApplicationUserService = connectedApplicationUserService;
    }

    private EventTicket getById(Long id) {
        return this.eventTicketRepository.getById(id).orElseThrow(
                () -> new ResourceNotFoundException("Event ticket with ID: " + id + " does not exist")
        );
    }

    @Transactional
    public EventTicket create(
            Instant eventDate,
            String ticketName,
            String decisions,
            String prognostics,
            Long eventId
    ) {
        Event event = this.eventService.getById(eventId);
        this.eventService.canUpdateCheck(event);

        EventTicket eventTicket = new EventTicket(event, eventDate, ticketName, decisions, prognostics, this.connectedApplicationUserService.getApplicationUser());
        this.eventTicketRepository.create(eventTicket);
        return eventTicket;
    }

    @Transactional
    public EventTicket update(Long id,
                              Instant eventDate,
                              String ticketName,
                              String decisions,
                              String prognostics,
                              Long eventId) {

        EventTicket eventTicketToUpdate = this.getById(id);

        this.canUpdateCheck(eventTicketToUpdate, eventId);

        eventTicketToUpdate.setLastUpdatedBy(this.connectedApplicationUserService.getApplicationUser());
        eventTicketToUpdate.setEventDate(eventDate);
        eventTicketToUpdate.setTicketName(ticketName);
        eventTicketToUpdate.setDecisions(decisions);
        eventTicketToUpdate.setPrognostics(prognostics);

        return this.eventTicketRepository.update(eventTicketToUpdate);

    }
    private void canUpdateCheck(EventTicket eventTicketToUpdate, Long eventId) {
        if (eventTicketToUpdate.getEvent() == null || eventTicketToUpdate.getEvent().getId() == null) {
            throw new ForbiddenActionException("No event associated with persisted event ticket " + eventTicketToUpdate.getId());
        }
        Event ticketEvent = eventTicketToUpdate.getEvent();
        if (!ticketEvent.getId().equals(eventId)) {
            throw new ForbiddenActionException("Persisted event ticket " + eventTicketToUpdate.getId() + " " +
                    "is associated with event id " + ticketEvent.getId() + ", given event id differs with " + eventId);
        }
        Event event = this.eventService.getById(eventId);
        this.eventService.canUpdateCheck(event);
    }


}
