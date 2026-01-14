package com.example.web.controller;

import com.example.domain.ApplicationUser;
import com.example.domain.Event;
import com.example.domain.EventTicket;
import com.example.service.EventService;
import com.example.service.EventTicketService;
import com.example.web.dto.filter.EventFilter;
import com.example.web.dto.input.EventCreateInput;
import com.example.web.dto.input.EventTicketInput;
import com.example.web.dto.input.EventUpdateInput;
import com.example.web.dto.response.EventDto;
import com.example.web.dto.response.EventTicketDto;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


/**
 * REST controller for managing events and corresponding tickets.
 *
 * <p>
 * All operations require an authenticated user and are protected by
 * role-based authorization. Read operations require read-only roles,
 * while create and update operations require read-write roles.
 * </p>
 *
 */
@Path("events")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class EventController {

    private final EventService eventService;
    private final EventTicketService eventTicketService;

    public EventController(EventService eventService, EventTicketService eventTicketService) {
        this.eventService = eventService;
        this.eventTicketService = eventTicketService;
    }


    /**
     * Retrieve all events matching the provided (optional) filter criteria.
     */
    @GET()
    @RolesAllowed({ApplicationUser.ROLE_MAINTENANCE_RO, ApplicationUser.ROLE_EMERGENCY_RO})
    public List<EventDto> getAll(@BeanParam EventFilter eventFilter) {
        return this.eventService.getAll(eventFilter).stream().map(EventDto::new).toList();
    }

    /**
     * Creates a new event.
     */
    @POST()
    @RolesAllowed({ApplicationUser.ROLE_MAINTENANCE_RW, ApplicationUser.ROLE_EMERGENCY_RW})
    public Response create(@Valid EventCreateInput eventInput) {
        Event eventToCreate = this.eventService.create(eventInput.title, eventInput.description, eventInput.eventType);
        return Response
                .status(Response.Status.CREATED)
                .entity(new EventDto(eventToCreate))
                .build();
    }

    /**
     * Update existing event.
     */
    @PUT()
    @Path("/{id}")
    @RolesAllowed({ApplicationUser.ROLE_MAINTENANCE_RW, ApplicationUser.ROLE_EMERGENCY_RW})
    public EventDto update(@PathParam("id") Long id, @Valid EventUpdateInput eventInput) {
        Event updatedEvent = this.eventService.update(id, eventInput.title, eventInput.description, eventInput.archived);
        return new EventDto(updatedEvent);
    }

    /**
     * Retrieves all tickets associated with a specific event.
     */
    @GET()
    @Path("/{id}/tickets")
    @RolesAllowed({ApplicationUser.ROLE_MAINTENANCE_RO, ApplicationUser.ROLE_EMERGENCY_RO})
    public List<EventTicketDto> getTicketsForEvent(@PathParam("id") Long eventId) {
        return this.eventService.getTicketsForEvent(eventId).stream().map(EventTicketDto::new).toList();
    }

    /**
     * Creates a new ticket for an event.
     */
    @POST()
    @Path("/{id}/tickets")
    @RolesAllowed({ApplicationUser.ROLE_MAINTENANCE_RW, ApplicationUser.ROLE_EMERGENCY_RW})
    public Response create(@PathParam("id") Long eventId, @Valid EventTicketInput ticketInput) {
        EventTicket createdTicket = this.eventTicketService.create(ticketInput.eventDate,
                ticketInput.ticketName,
                ticketInput.decisions,
                ticketInput.prognostics,
                eventId
        );
        return Response
                .status(Response.Status.CREATED)
                .entity(new EventTicketDto(createdTicket))
                .build();
    }

    /**
     * Updates a ticket for an event.
     */
    @PUT()
    @Path("/{id}/tickets/{ticket_id}")
    @RolesAllowed({ApplicationUser.ROLE_MAINTENANCE_RW, ApplicationUser.ROLE_EMERGENCY_RW})
    public EventTicketDto update(@PathParam("id") Long eventId, @PathParam("ticket_id") Long ticketId, @Valid EventTicketInput ticketInput) {
        EventTicket updatedTicket = this.eventTicketService.update(
                ticketId,
                ticketInput.eventDate,
                ticketInput.ticketName,
                ticketInput.decisions,
                ticketInput.prognostics,
                eventId
        );
        return new EventTicketDto(updatedTicket);
    }


}
