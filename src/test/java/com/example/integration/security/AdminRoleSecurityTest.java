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
public class AdminRoleSecurityTest extends  OidcTestBase {

    @AfterEach
    void resetDbAfterEach() {
        resetDb();
    }

    @Test
    public void adminRoleCanViewCreateAndEditAllEvents() {

        String token = getAccessToken(
                TEST_KEYCLOAK_ADMIN_USERNAME,
                TEST_KEYCLOAK_ADMIN_USERNAME
        );

        // --------------------------
        // VIEW EVENTS
        // --------------------------

        // View maintenance events - ALLOWED
        getEventsWithType(token, EventType.MAINTENANCE)
                .statusCode(200)
                .body("size()", is(2));

        // View emergency events - ALLOWED
        getEventsWithType(token, EventType.EMERGENCY)
                .statusCode(200)
                .body("size()", is(2));

        // View all events - ALLOWED
        getAllEvents(token)
                .statusCode(200)
                .body("size()", is(4));

        // --------------------------
        // CREATE EVENTS
        // --------------------------

        // Create maintenance event - ALLOWED
        ValidatableResponse maintenanceEventResp = postEventWithType(token, EventType.MAINTENANCE)
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo(TEST_EVENT_TITLE))
                .body("description", equalTo(TEST_EVENT_DESCRIPTION))
                .body("eventType", equalTo(EventType.MAINTENANCE.name()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue())
                .body("archivedAt", nullValue())
                .body("createdBy.email", equalTo(TEST_KEYCLOAK_ADMIN_USER_EMAIL))
                .body("lastUpdatedBy.email", equalTo(TEST_KEYCLOAK_ADMIN_USER_EMAIL));

        Long newMaintenanceEventId = ((Integer) maintenanceEventResp.extract().path("id")).longValue();

        // Create emergency event - ALLOWED
        ValidatableResponse emergencyEventResp = postEventWithType(token, EventType.EMERGENCY)
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo(TEST_EVENT_TITLE))
                .body("description", equalTo(TEST_EVENT_DESCRIPTION))
                .body("eventType", equalTo(EventType.EMERGENCY.name()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue())
                .body("archivedAt", nullValue())
                .body("createdBy.email", equalTo(TEST_KEYCLOAK_ADMIN_USER_EMAIL))
                .body("lastUpdatedBy.email", equalTo(TEST_KEYCLOAK_ADMIN_USER_EMAIL));

        Long newEmergencyEventId = ((Integer) emergencyEventResp.extract().path("id")).longValue();

        // --------------------------
        // TICKETS
        // --------------------------


        // Create ticket for the newly created maintenance event - ALLOWED
        ValidatableResponse maintenanceTicketResp = createTicket(token, newMaintenanceEventId)
                .statusCode(201)
                .body("id", notNullValue())
                .body("ticketName", equalTo(TEST_TICKET_NAME))
                .body("decisions", equalTo(TEST_TICKET_DECISIONS))
                .body("prognostics", equalTo(TEST_TICKET_PROGNOSTICS))
                .body("eventId", is(newMaintenanceEventId.intValue()));

        Long newMaintenanceTicketId = ((Integer) maintenanceTicketResp.extract().path("id")).longValue();

        // Get tickets for the event - ALLOWED
        getTicketsForEvent(token, newMaintenanceEventId)
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].ticketName", equalTo(TEST_TICKET_NAME));

        // Update ticket - ALLOWED
        updateTicket(token, newMaintenanceEventId, newMaintenanceTicketId)
                .statusCode(200)
                .body("id", is(newMaintenanceTicketId.intValue()))
                .body("ticketName", equalTo(TEST_TICKET_NAME_EDITED))
                .body("decisions", equalTo(TEST_TICKET_DECISIONS_EDITED))
                .body("prognostics", equalTo(TEST_TICKET_PROGNOSTICS_EDITED));


        // Create ticket for the newly created emergency event - ALLOWED
        ValidatableResponse emergencyTicketResp = createTicket(token, newEmergencyEventId)
                .statusCode(201)
                .body("id", notNullValue())
                .body("ticketName", equalTo(TEST_TICKET_NAME))
                .body("decisions", equalTo(TEST_TICKET_DECISIONS))
                .body("prognostics", equalTo(TEST_TICKET_PROGNOSTICS))
                .body("eventId", is(newEmergencyEventId.intValue()));

        Long newEmergencyTicketId = ((Integer) emergencyTicketResp.extract().path("id")).longValue();

        // Get tickets for the event - ALLOWED
        getTicketsForEvent(token, newEmergencyEventId)
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].ticketName", equalTo(TEST_TICKET_NAME));

        // Update ticket - ALLOWED
        updateTicket(token, newEmergencyEventId, newEmergencyTicketId)
                .statusCode(200)
                .body("id", is(newEmergencyTicketId.intValue()))
                .body("ticketName", equalTo(TEST_TICKET_NAME_EDITED))
                .body("decisions", equalTo(TEST_TICKET_DECISIONS_EDITED))
                .body("prognostics", equalTo(TEST_TICKET_PROGNOSTICS_EDITED));

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
                .body("createdBy.email", equalTo(TEST_KEYCLOAK_ADMIN_USER_EMAIL))
                .body("lastUpdatedBy.email", equalTo(TEST_KEYCLOAK_ADMIN_USER_EMAIL));

        // Edit emergency event - ALLOWED
        editPersistedEvent(token, newEmergencyEventId, false)
                .statusCode(200)
                .body("id", is(newEmergencyEventId.intValue()))
                .body("title", equalTo(TEST_EVENT_EDITED_TITLE))
                .body("description", equalTo(TEST_EVENT_EDITED_DESCRIPTION))
                .body("eventType", equalTo(EventType.EMERGENCY.name()))
                .body("updatedAt", notNullValue())
                .body("archivedAt", nullValue())
                .body("createdBy.email", equalTo(TEST_KEYCLOAK_ADMIN_USER_EMAIL))
                .body("lastUpdatedBy.email", equalTo(TEST_KEYCLOAK_ADMIN_USER_EMAIL));
    }

}
