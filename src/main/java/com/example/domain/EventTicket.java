package com.example.domain;

import com.example.persistence.listener.AuditableEntityListener;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@EntityListeners(AuditableEntityListener.class)
@Table(name = "EVENT_TICKET")
public class EventTicket implements AuditableEntity {


    public EventTicket() {
    }

    public EventTicket(Event event, Instant eventDate, String ticketName, String decisions, String prognostics, ApplicationUser createdBy) {
        this.event = event;
        this.eventDate = eventDate;
        this.ticketName = ticketName;
        this.decisions = decisions;
        this.prognostics = prognostics;
        this.createdBy = createdBy;
        this.lastUpdatedBy = createdBy;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EVENT_ID", nullable = false)
    private Event event;

    @Column(name = "EVENT_DATE")
    private Instant eventDate;

    @Column(name = "TICKET_NAME", length = 64)
    private String ticketName;

    @Column(name = "DECISIONS", length = 2048)
    private String decisions;

    @Column(name = "PROGNOSTICS", length = 2048)
    private String prognostics;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;

    @Column(name = "LAST_UPDATED_AT", nullable = false)
    private Instant lastUpdatedAt;

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

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public String getDecisions() {
        return decisions;
    }

    public void setDecisions(String decisions) {
        this.decisions = decisions;
    }

    public String getPrognostics() {
        return prognostics;
    }

    public void setPrognostics(String prognostics) {
        this.prognostics = prognostics;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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

    public void setLastUpdatedBy(ApplicationUser lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Instant lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
