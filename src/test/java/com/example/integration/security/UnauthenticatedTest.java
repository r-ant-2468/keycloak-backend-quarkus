package com.example.integration.security;

import com.example.domain.enums.EventType;
import com.example.integration.OidcTestBase;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UnauthenticatedTest  extends OidcTestBase {

    @Test
    public void unauthorizedUserHasNoAccessToEventsAndTicketEndPoints() {


        // --------------------------
        // VIEW EVENTS
        // --------------------------

        // View events with type maintenance - NOT ALLOWED
        getEventsWithType(null, EventType.MAINTENANCE)
                .statusCode(401);


        // View events with type emergency - NOT ALLOWED
        getEventsWithType(null, EventType.EMERGENCY)
                .statusCode(401);

        // View all events - NOT ALLOWED
        getAllEvents(null)
                .statusCode(401);

        // --------------------------
        // CREATE EVENTS
        // --------------------------

        // Create maintenance event - NOT ALLOWED
        postEventWithType(null, EventType.MAINTENANCE)
                .statusCode(401);

        // Create emergency event - NOT ALLOWED
        postEventWithType(null, EventType.EMERGENCY)
                .statusCode(401);

        // --------------------------
        // TICKETS
        // --------------------------

        // Create ticket for the newly created maintenance event - NOT ALLOWED
        ValidatableResponse ticketResp = createTicket(null, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED)
                .statusCode(401);

        // Get tickets for the event - NOT ALLOWED
        getTicketsForEvent(null, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED)
                .statusCode(401);

        // Update ticket - NOT ALLOWED
        updateTicket(null, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED, TEST_MAINTENANCE_TICKET_ID_EVENT_NON_ARCHIVED)
                .statusCode(401);

        // --------------------------
        // EDIT EVENTS
        // --------------------------

        // Edit maintenance event - NOT ALLOWED
        editPersistedEvent(null, TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED, false)
                .statusCode(401);

        // Edit emergency event - NOT ALLOWED
        editPersistedEvent(null, TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED, false)
                .statusCode(401);
    }


}
