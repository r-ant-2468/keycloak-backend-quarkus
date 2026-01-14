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
public class MaintenanceRolesSecurityTest extends OidcTestBase {


    @AfterEach
    void resetDbAfterEach() {
        resetDb(); // calls parent logic
    }

    @Test
    public void maintenanceRWRoleCanCreateAndEditMaintenanceEventsOnly() {

        String token = getAccessToken(
                TEST_KEYCLOAK_MAINTENANCE_USER_RW_USERNAME,
                TEST_KEYCLOAK_MAINTENANCE_USER_RW_USERNAME
        );

        // --------------------------
        // VIEW EVENTS
        // --------------------------

        // View events with type maintenance - ALLOWED
        getEventsWithType(token, EventType.MAINTENANCE)
                .statusCode(200)
                .body("size()", is(2)); // checks that there is 2 maintenance events

        // View events with type emergency - NOT ALLOWED
        getEventsWithType(token, EventType.EMERGENCY)
                .statusCode(404);

        // View all events (maintenance & emergency) - NOT ALLOWED
        getAllEvents(token)
                .statusCode(404);

        // --------------------------
        // CREATE EVENTS
        // --------------------------

        // Create event type emergency - NOT ALLOWED
        postEventWithType(token, EventType.EMERGENCY)
                .statusCode(404);

        // Create event type maintenance - ALLOWED
        ValidatableResponse maintenanceEventResp = postEventWithType(token, EventType.MAINTENANCE)
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo(TEST_EVENT_TITLE))
                .body("description", equalTo(TEST_EVENT_DESCRIPTION))
                .body("eventType", equalTo(EventType.MAINTENANCE.name()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue())
                .body("archivedAt", nullValue())
                .body("createdBy.email", equalTo(TEST_KEYCLOAK_MAINTENANCE_USER_RW_EMAIL))
                .body("lastUpdatedBy.email", equalTo(TEST_KEYCLOAK_MAINTENANCE_USER_RW_EMAIL));

        Long newMaintenanceEventId = ((Integer) maintenanceEventResp.extract().path("id")).longValue();

        getEventsWithType(token, EventType.MAINTENANCE)
                .statusCode(200)
                .body("size()", is(3)); // checks that there is 3 maintenance events


        // --------------------------
        // TICKETS
        // --------------------------

        // Create ticket for the newly created maintenance event - ALLOWED
        ValidatableResponse ticketResp = createTicket(token, newMaintenanceEventId)
                .statusCode(201)
                .body("id", notNullValue())
                .body("ticketName", equalTo(TEST_TICKET_NAME))
                .body("decisions", equalTo(TEST_TICKET_DECISIONS))
                .body("prognostics", equalTo(TEST_TICKET_PROGNOSTICS))
                .body("eventId", is(newMaintenanceEventId.intValue()));

        Long newTicketId = ((Integer) ticketResp.extract().path("id")).longValue();

        // Get tickets for the event - ALLOWED
        getTicketsForEvent(token, newMaintenanceEventId)
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].ticketName", equalTo(TEST_TICKET_NAME));

        // Update ticket - ALLOWED
        updateTicket(token, newMaintenanceEventId, newTicketId)
                .statusCode(200)
                .body("id", is(newTicketId.intValue()))
                .body("ticketName", equalTo(TEST_TICKET_NAME_EDITED))
                .body("decisions", equalTo(TEST_TICKET_DECISIONS_EDITED))
                .body("prognostics", equalTo(TEST_TICKET_PROGNOSTICS_EDITED));


        // Create ticket for emergency event - NOT ALLOWED
        createTicket(token, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED)
                .statusCode(404);

        // Get tickets for emergency event - NOT ALLOWED
        getTicketsForEvent(token, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED)
                .statusCode(404);

        // Update emergency ticket - NOT ALLOWED
        updateTicket(token, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED, TEST_EMERGENCY_TICKET_ID_EVENT_NON_ARCHIVED)
                .statusCode(404);


        // --------------------------
        // EDIT EVENTS
        // --------------------------

        // Edit maintenance event - ALLOWED
        editPersistedEvent(token, newMaintenanceEventId, false)
                .statusCode(200)
                .body("id", is(newMaintenanceEventId.intValue()))
                .body("title", equalTo(TEST_EVENT_EDITED_TITLE))
                .body("description", equalTo(TEST_EVENT_EDITED_DESCRIPTION))
                .body("eventType", equalTo(EventType.MAINTENANCE.name()))
                .body("updatedAt", notNullValue())
                .body("archivedAt", nullValue())
                .body("createdBy.email", equalTo(TEST_KEYCLOAK_MAINTENANCE_USER_RW_EMAIL))
                .body("lastUpdatedBy.email", equalTo(TEST_KEYCLOAK_MAINTENANCE_USER_RW_EMAIL));

        // Edit emergency event - NOT ALLOWED
        editPersistedEvent(token, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED, false)
                .statusCode(404);




    }


    @Test
    public void maintenanceRORoleCanViewMaintenanceEventsOnly() {

        String token = getAccessToken(
                TEST_KEYCLOAK_MAINTENANCE_USER_RO_USERNAME,
                TEST_KEYCLOAK_MAINTENANCE_USER_RO_USERNAME
        );

        // --------------------------
        // VIEW EVENTS
        // --------------------------

        // View events with type maintenance - ALLOWED
        getEventsWithType(token, EventType.MAINTENANCE)
                .statusCode(200)
                .body("size()", is(2));

        // View events with type emergency - NOT ALLOWED
        getEventsWithType(token, EventType.EMERGENCY)
                .statusCode(404);

        // View all events - NOT ALLOWED
        getAllEvents(token)
                .statusCode(404);

        // --------------------------
        // CREATE EVENTS
        // --------------------------

        // Create maintenance event - NOT ALLOWED
        postEventWithType(token, EventType.MAINTENANCE)
                .statusCode(403);

        // Create emergency event - NOT ALLOWED
        postEventWithType(token, EventType.EMERGENCY)
                .statusCode(403);



        // --------------------------
        // TICKETS
        // --------------------------

        // Create ticket for emergency event - NOT ALLOWED
        createTicket(token, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED)
                .statusCode(403);

        // Get tickets for emergency event - NOT ALLOWED
        getTicketsForEvent(token, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED)
                .statusCode(404);

        // Update emergency ticket - NOT ALLOWED
        updateTicket(token, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED, TEST_EMERGENCY_TICKET_ID_EVENT_NON_ARCHIVED)
                .statusCode(403);

        // Create ticket for maintenance event - NOT ALLOWED
        createTicket(token, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED)
                .statusCode(403);

        // Get tickets for maintenance event - ALLOWED
        getTicketsForEvent(token, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED)
                .statusCode(200)
                .body("size()", is(1));

        // Update maintenance ticket - NOT ALLOWED
        updateTicket(token, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED, TEST_MAINTENANCE_TICKET_ID_EVENT_NON_ARCHIVED)
                .statusCode(403);


        // --------------------------
        // EDIT EVENTS
        // --------------------------

        // Edit maintenance event - NOT ALLOWED
        editPersistedEvent(token, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED, false)
                .statusCode(403);

        // Edit emergency event - NOT ALLOWED
        editPersistedEvent(token, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED, false)
                .statusCode(403);
    }


}
