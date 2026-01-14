package com.example.integration;

import com.example.domain.enums.EventType;
import com.example.web.dto.input.EventCreateInput;
import com.example.web.dto.input.EventTicketInput;
import com.example.web.dto.input.EventUpdateInput;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.OffsetDateTime;
import java.util.List;

public abstract class OidcTestBase {

    @Inject
    protected EntityManager em;

    @Transactional
    protected void resetDb() {
        // Delete child tables first (tickets → events → users)
        em.createNativeQuery(
                        "DELETE FROM EVENT_TICKET WHERE ID NOT IN (:ticketIds)")
                .setParameter("ticketIds", List.of(
                        TEST_MAINTENANCE_TICKET_ID_EVENT_NON_ARCHIVED,
                        TEST_MAINTENANCE_TICKET_ID_EVENT_ARCHIVED,
                        TEST_EMERGENCY_TICKET_ID_EVENT_NON_ARCHIVED,
                        TEST_EMERGENCY_TICKET_ID_EVENT_ARCHIVED
                ))
                .executeUpdate();

        em.createNativeQuery(
                        "DELETE FROM EVENT WHERE ID NOT IN (:eventIds)")
                .setParameter("eventIds", List.of(
                        TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED,
                        TEST_MAINTENANCE_EVENT_ID_ARCHIVED,
                        TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED,
                        TEST_EMERGENCY_EVENT_ID_ARCHIVED
                ))
                .executeUpdate();

        em.createNativeQuery(
                        "DELETE FROM APPLICATION_USER WHERE ID NOT IN (:userIds)")
                .setParameter("userIds", List.of(
                        TEST_USER_ID_MAINTENANCE_RW,
                        TEST_USER_ID_EMERGENCY_RW
                ))
                .executeUpdate();
    }

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String oidcUrl;

    //// import.sql values
    // --------------------------
    // Event IDs
    // --------------------------
    protected static final Long TEST_MAINTENANCE_EVENT_ID_NON_ARCHIVED = 10001L;
    protected static final Long TEST_MAINTENANCE_EVENT_ID_ARCHIVED = 10002L;
    protected static final Long TEST_EMERGENCY_EVENT_ID_NON_ARCHIVED = 10003L;
    protected static final Long TEST_EMERGENCY_EVENT_ID_ARCHIVED = 10004L;
    // --------------------------
    // TICKET IDs
    // --------------------------
    protected static final Long TEST_MAINTENANCE_TICKET_ID_EVENT_NON_ARCHIVED = 10001L;
    protected static final Long TEST_MAINTENANCE_TICKET_ID_EVENT_ARCHIVED = 10002L;
    protected static final Long TEST_EMERGENCY_TICKET_ID_EVENT_NON_ARCHIVED = 10003L;
    protected static final Long TEST_EMERGENCY_TICKET_ID_EVENT_ARCHIVED = 10004L;

    // --------------------------
    // Users
    // --------------------------
    protected static final String TEST_USER_ID_MAINTENANCE_RW = "user-1-uuid";
    protected static final String TEST_USER_ID_EMERGENCY_RW = "user-2-uuid";
    ////

    //// Keycloak values
    // --------------------------
    // Users
    // --------------------------

    protected static String TEST_KEYCLOAK_ADMIN_USERNAME = "admin";
    protected static String TEST_KEYCLOAK_MAINTENANCE_USER_RW_USERNAME = "maintenanceuserrw";
    protected static String TEST_KEYCLOAK_EMERGENCY_USER_RW_USERNAME = "emergencyuserrw";
    protected static String TEST_KEYCLOAK_MAINTENANCE_USER_RO_USERNAME = "maintenanceuserro";
    protected static String TEST_KEYCLOAK_EMERGENCY_USER_RO_USERNAME = "emergencyuserro";

    protected static String TEST_KEYCLOAK_ADMIN_USER_EMAIL = "admin@example.com";
    protected static String TEST_KEYCLOAK_MAINTENANCE_USER_RW_EMAIL = "maintenance-rw@example.com";
    protected static String TEST_KEYCLOAK_EMERGENCY_USER_RW_EMAIL = "emergency-rw@example.com";



    // --------------------------
    // Event test values
    // --------------------------
    protected static String TEST_EVENT_TITLE = "Test title";
    protected static String TEST_EVENT_EDITED_TITLE = "Test edited title";
    protected static String TEST_EVENT_DESCRIPTION = "Test title";
    protected static String TEST_EVENT_EDITED_DESCRIPTION = "Test title";
    // --------------------------
    // Ticket test values
    // --------------------------
    protected static final String TEST_TICKET_NAME = "Test ticket";
    protected static final String TEST_TICKET_NAME_EDITED = "Test ticket edited";
    protected static final String TEST_TICKET_DECISIONS = "Test decisions";
    protected static final String TEST_TICKET_DECISIONS_EDITED = "Edited decisions";
    protected static final String TEST_TICKET_PROGNOSTICS = "Test prognostics";
    protected static final String TEST_TICKET_PROGNOSTICS_EDITED = "Edited prognostics";

    protected String getAccessToken(String username, String password) {
        return RestAssured.given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("client_id", "event-app")
                .formParam("scope", "openid")
                .formParam("username", username)
                .formParam("password", password)
                .post(oidcUrl + "/protocol/openid-connect/token")
                .then()
                .statusCode(200)
                .extract()
                .path("access_token");
    }

    protected ValidatableResponse postEventWithType(String token, EventType eventType) {

        EventCreateInput input = new EventCreateInput();
        input.eventType = eventType;
        input.title = TEST_EVENT_TITLE;
        input.description = TEST_EVENT_DESCRIPTION;

        if(token != null) {
            return RestAssured.given()
                    .auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(input)
                    .when()
                    .post("/events")
                    .then();
        } else{
            return RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(input)
                    .when()
                    .post("/events")
                    .then();
        }

    }
    protected ValidatableResponse editPersistedEvent(String token, Long eventId, boolean archived) {
        EventUpdateInput input = new EventUpdateInput();
        input.title = TEST_EVENT_EDITED_TITLE;
        input.description = TEST_EVENT_EDITED_DESCRIPTION;
        input.archived = archived;

        if (token != null) {
            return RestAssured.given()
                    .auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(input)
                    .when()
                    .put("/events/" + eventId)
                    .then();
        } else {
            return RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(input)
                    .when()
                    .put("/events/" + eventId)
                    .then();
        }
    }

    protected ValidatableResponse getEventsWithType(String token, EventType eventType) {
        if (token != null) {
            return RestAssured.given()
                    .auth().oauth2(token)
                    .queryParam("eventType", eventType)
                    .when()
                    .get("/events")
                    .then();
        } else {
            return RestAssured.given()
                    .queryParam("eventType", eventType)
                    .when()
                    .get("/events")
                    .then();
        }
    }

    protected ValidatableResponse getAllEvents(String token) {
        if (token != null) {
            return RestAssured.given()
                    .auth().oauth2(token)
                    .when()
                    .get("/events")
                    .then();
        } else {
            return RestAssured.given()
                    .when()
                    .get("/events")
                    .then();
        }
    }

    protected ValidatableResponse getTicketsForEvent(String token, Long eventId) {
        if (token != null) {
            return RestAssured.given()
                    .auth().oauth2(token)
                    .when()
                    .get("/events/" + eventId + "/tickets")
                    .then();
        } else {
            return RestAssured.given()
                    .when()
                    .get("/events/" + eventId + "/tickets")
                    .then();
        }
    }

    protected ValidatableResponse createTicket(String token, Long eventId) {
        EventTicketInput input = new EventTicketInput();
        input.ticketName = TEST_TICKET_NAME;
        input.decisions = TEST_TICKET_DECISIONS;
        input.prognostics = TEST_TICKET_PROGNOSTICS;
        input.eventDate = OffsetDateTime.now().toInstant();

        if (token != null) {
            return RestAssured.given()
                    .auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(input)
                    .when()
                    .post("/events/" + eventId + "/tickets")
                    .then();
        } else {
            return RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(input)
                    .when()
                    .post("/events/" + eventId + "/tickets")
                    .then();
        }
    }

    protected ValidatableResponse updateTicket(String token, Long eventId, Long ticketId) {
        EventTicketInput input = new EventTicketInput();
        input.ticketName = TEST_TICKET_NAME_EDITED;
        input.decisions = TEST_TICKET_DECISIONS_EDITED;
        input.prognostics = TEST_TICKET_PROGNOSTICS_EDITED;
        input.eventDate = OffsetDateTime.now().toInstant();

        if (token != null) {
            return RestAssured.given()
                    .auth().oauth2(token)
                    .contentType(ContentType.JSON)
                    .body(input)
                    .when()
                    .put("/events/" + eventId + "/tickets/" + ticketId)
                    .then();
        } else {
            return RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(input)
                    .when()
                    .put("/events/" + eventId + "/tickets/" + ticketId)
                    .then();
        }
    }


}
