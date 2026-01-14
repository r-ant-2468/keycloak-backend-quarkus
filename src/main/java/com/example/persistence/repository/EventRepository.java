package com.example.persistence.repository;

import com.example.domain.Event;
import com.example.web.dto.filter.EventFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

import java.util.*;

@ApplicationScoped
public class EventRepository {

    private final EntityManager entityManager;

    public EventRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public List<Event> getAll(EventFilter eventFilter) {


        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> event = cq.from(Event.class);
        List<Predicate> predicates = convertFilterToPredicates(eventFilter, cb, event);

        cq.where(predicates.toArray(new Predicate[0]));
        return this.entityManager.createQuery(cq).getResultList();
    }

    public Optional<Event> getById(Long id) {
        return Optional.ofNullable(this.entityManager.find(Event.class, id));
    }

    public void create(Event event) {
        this.entityManager.persist(event);
    }

    public Event update(Event event) {
        return this.entityManager.merge(event);
    }


    private List<Predicate> convertFilterToPredicates(EventFilter eventFilter, CriteriaBuilder cb, Root<Event> event) {
        List<Predicate> predicates = new ArrayList<>();
        Boolean archived = eventFilter.getArchived();
        if (archived != null) {
            if (archived) {
                predicates.add(cb.isNotNull(event.get("archivedAt")));
            } else {
                predicates.add(cb.isNull(event.get("archivedAt")));
            }
        }
        if (eventFilter.getEventType() != null) {
            predicates.add(cb.equal(event.get("eventType"), eventFilter.getEventType()));
        }

        return predicates;

    }

}
