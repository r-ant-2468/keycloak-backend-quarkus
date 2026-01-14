package com.example.integration.security;

import com.example.domain.enums.EventType;
import com.example.integration.OidcTestBase;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class EmergencyRolesSecurityTest extends  OidcTestBase {

    @AfterEach
    void resetDbAfterEach() {
        resetDb(); // calls parent logic
    }

    @Test
    public void emergencyRWRoleCanCreateAndEditEmergencyEventsOnly() {

        String token = getAccessToken(
                TEST_KEYCLOAK_EMERGENCY_USER_RW_USERNAME,
                TEST_KEYCLOAK_EMERGENCY_USER_RW_USERNAME
        );

        // --------------------------
        // VIEW EVENTS
        // --------------------------

        // View events with type emergency - ALLOWED
        getEventsWithType(token, EventType.EMERGENCY)
                .statusCode(200)
                .body("size()", is(2)); // checks that there is 2 emergency events

        // View events with type maintenance - NOT ALLOWED
        getEventsWithType(token, EventType.MAINTENANCE)
                .statusCode(404);

        // View all events (maintenance & emergency) - NOT ALLOWED
        getAllEvents(token)
                .statusCode(404);

        // --------------------------
        // CREATE EVENTS
        // --------------------------

        // Create event type emergency - ALLOWED
        ValidatableResponse emergencyEventResp = postEventWithType(token, EventType.EMERGENCY)
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo(TEST_EVENT_TITLE))
                .body("description", equalTo(TEST_EVENT_DESCRIPTION))
                .body("eventType", equalTo(EventType.EMERGENCY.name()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue())
                .body("archivedAt", nullValue())
                .body("createdBy.email", equalTo(TEST_KEYCLOAK_EMERGENCY_USER_RW_EMAIL))
                .body("lastUpdatedBy.email", equalTo(TEST_KEYCLOAK_EMERGENCY_USER_RW_EMAIL));

        Long newEmergencyEventId = ((Integer) emergencyEventResp.extract().path("id")).longValue();


        getEventsWithType(token, EventType.EMERGENCY)
                .statusCode(200)
                .body("size()", is(3)); // checks that there is 3 emergency events

        // Create event type maintenance - NOT ALLOWED
        postEventWithType(token, EventType.MAINTENANCE)
                .statusCode(404);

        // --------------------------
        // TICKETS
        // --------------------------

        // Create ticket for the newly created emergency event - ALLOWED
        ValidatableResponse ticketResp = createTicket(token, newEmergencyEventId)
                .statusCode(201)
                .body("id", notNullValue())
                .body("ticketName", equalTo(TEST_TICKET_NAME))
                .body("decisions", equalTo(TEST_TICKET_DECISIONS))
                .body("prognostics", equalTo(TEST_TICKET_PROGNOSTICS))
                .body("eventId", is(newEmergencyEventId.intValue()));

        Long newTicketId = ((Integer) ticketResp.extract().path("id")).longValue();

        // Get tickets for the event - ALLOWED
        getTicketsForEvent(token, newEmergencyEventId)
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].ticketName", equalTo(TEST_TICKET_NAME));

        // Update ticket - ALLOWED
        updateTicket(token, newEmergencyEventId, newTicketId)
                .statusCode(200)
                .body("id", is(newTicketId.intValue()))
                .body("ticketName", equalTo(TEST_TICKET_NAME_EDITED))
                .body("decisions", equalTo(TEST_TICKET_DECISIONS_EDITED))
                .body("prognostics", equalTo(TEST_TICKET_PROGNOSTICS_EDITED));

        // Create ticket for maintenance event - NOT ALLOWED
        createTicket(token, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED)
                .statusCode(404);

        // Get tickets for maintenance event - NOT ALLOWED
        getTicketsForEvent(token, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED)
                .statusCode(404);

        // Update maintenance ticket - NOT ALLOWED
        updateTicket(token, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED, TEST_MAINTENANCE_TICKET_ID_EVENT_NON_ARCHIVED)
                .statusCode(404);

        // --------------------------
        // EDIT EVENTS
        // --------------------------

        // Edit event type emergency - ALLOWED
        editPersistedEvent(token, newEmergencyEventId, false)
                .statusCode(200)
                .body("id", is(newEmergencyEventId.intValue()))
                .body("title", equalTo(TEST_EVENT_EDITED_TITLE))
                .body("description", equalTo(TEST_EVENT_EDITED_DESCRIPTION))
                .body("eventType", equalTo(EventType.EMERGENCY.name()))
                .body("updatedAt", notNullValue())
                .body("archivedAt", nullValue())
                .body("createdBy.email", equalTo(TEST_KEYCLOAK_EMERGENCY_USER_RW_EMAIL))
                .body("lastUpdatedBy.email", equalTo(TEST_KEYCLOAK_EMERGENCY_USER_RW_EMAIL));

        // Edit event type maintenance - NOT ALLOWED
        editPersistedEvent(token, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED, false)
                .statusCode(404);
    }

    @Test
    public void emergencyRORoleCanViewEmergencyEventsOnly() {

        String token = getAccessToken(
                TEST_KEYCLOAK_EMERGENCY_USER_RO_USERNAME,
                TEST_KEYCLOAK_EMERGENCY_USER_RO_USERNAME
        );

        // --------------------------
        // VIEW EVENTS
        // --------------------------

        // View events with type emergency - ALLOWED
        getEventsWithType(token, EventType.EMERGENCY)
                .statusCode(200)
                .body("size()", is(2));

        // View events with type maintenance - NOT ALLOWED
        getEventsWithType(token, EventType.MAINTENANCE)
                .statusCode(404);

        // View all events - NOT ALLOWED
        getAllEvents(token)
                .statusCode(404);

        // --------------------------
        // CREATE EVENTS
        // --------------------------

        // Create event type emergency - NOT ALLOWED
        postEventWithType(token, EventType.EMERGENCY)
                .statusCode(403);

        // Create event type maintenance - NOT ALLOWED
        postEventWithType(token, EventType.MAINTENANCE)
                .statusCode(403);


        // --------------------------
        // TICKETS
        // --------------------------

        // Create ticket for emergency event - NOT ALLOWED
        createTicket(token, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED)
                .statusCode(403);

        // Get tickets for emergency event - ALLOWED
        getTicketsForEvent(token, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED)
                .statusCode(200)
                .body("size()", is(1));

        // Update emergency ticket - NOT ALLOWED
        updateTicket(token, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED, TEST_EMERGENCY_TICKET_ID_EVENT_NON_ARCHIVED)
                .statusCode(403);

        // Create ticket for maintenance event - NOT ALLOWED
        createTicket(token, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED)
                .statusCode(403);

        // Get tickets for maintenance event - NOT ALLOWED
        getTicketsForEvent(token, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED)
                .statusCode(404);

        // Update maintenance ticket - NOT ALLOWED
        updateTicket(token, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED, TEST_MAINTENANCE_TICKET_ID_EVENT_NON_ARCHIVED)
                .statusCode(403);


        // --------------------------
        // EDIT EVENTS
        // --------------------------

        // Edit emergency event - NOT ALLOWED
        editPersistedEvent(token, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED, false)
                .statusCode(403);

        // Edit maintenance event - NOT ALLOWED
        editPersistedEvent(token, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED, false)
                .statusCode(403);
    }

}
