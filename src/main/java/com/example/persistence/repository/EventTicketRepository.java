package com.example.persistence.repository;

import com.example.domain.Event;
import com.example.domain.EventTicket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EventTicketRepository {

    private final EntityManager entityManager;

    public EventTicketRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<EventTicket> getAllForEvent(Event event) {
        TypedQuery<EventTicket> query = entityManager.createQuery(
                "FROM EventTicket e WHERE e.event = :event", EventTicket.class);
        query.setParameter("event", event);
        return query.getResultList();
    }

    public Optional<EventTicket> getById(Long id) {
        return Optional.ofNullable(this.entityManager.find(EventTicket.class, id));
    }

    public void create(EventTicket eventTicket) {
        this.entityManager.persist(eventTicket);
    }

    public EventTicket update(EventTicket eventTicket) {
        return this.entityManager.merge(eventTicket);
    }


}
