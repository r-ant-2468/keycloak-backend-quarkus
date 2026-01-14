package com.example.web.dto.response;

import com.example.domain.ApplicationUser;
import com.example.domain.EventTicket;

import java.time.Instant;

public class EventTicketDto {

    public final Long id;
    public final Instant eventDate;
    public final String ticketName;
    public final String decisions;
    public final String prognostics;
    public final Instant createdAt;
    public final ApplicationUserDto createdBy;
    public final Instant lastUpdatedAt;
    public final Long eventId;

    public EventTicketDto(EventTicket eventTicket) {
        this.id = eventTicket.getId();
        this.eventDate = eventTicket.getEventDate();
        this.ticketName = eventTicket.getTicketName();
        this.decisions = eventTicket.getDecisions();
        this.prognostics = eventTicket.getPrognostics();
        this.createdAt = eventTicket.getCreatedAt();
        this.createdBy = new ApplicationUserDto(eventTicket.getCreatedBy());
        this.lastUpdatedAt = eventTicket.getLastUpdatedAt();
        this.eventId = eventTicket.getEvent().getId();

    }
}
