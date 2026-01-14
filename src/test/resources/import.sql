-- --------------------------
-- Users
-- --------------------------
INSERT INTO APPLICATION_USER (ID, EMAIL, FIRST_NAME, LAST_NAME)
VALUES ('user-1-uuid', 'maintenance-rw@testimport.com', 'Maintenance', 'RW');

INSERT INTO APPLICATION_USER (ID, EMAIL, FIRST_NAME, LAST_NAME)
VALUES ('user-2-uuid', 'emergency-rw@testimport.com', 'Emergency', 'RW');


-- --------------------------
-- Events
-- --------------------------
-- Non-archived Maintenance
INSERT INTO EVENT (
    ID, TITLE, DESCRIPTION, EVENT_TYPE,
    CREATED_BY_ID, LAST_UPDATED_BY_ID,
    CREATED_AT, LAST_UPDATED_AT
)
VALUES (
           10001, 'Maintenance Event 1', 'Test maintenance event', 'MAINTENANCE',
           'user-1-uuid', 'user-1-uuid',
           NOW(), NOW()
       );

-- Archived Maintenance
INSERT INTO EVENT (
    ID, TITLE, DESCRIPTION, EVENT_TYPE,
    CREATED_BY_ID, LAST_UPDATED_BY_ID,
    CREATED_AT, LAST_UPDATED_AT,
    ARCHIVED_AT
)
VALUES (
           10002, 'Maintenance Event 2', 'Archived maintenance event', 'MAINTENANCE',
           'user-1-uuid', 'user-1-uuid',
           NOW(), NOW(),
           NOW()
       );

-- Non-archived Emergency
INSERT INTO EVENT (
    ID, TITLE, DESCRIPTION, EVENT_TYPE,
    CREATED_BY_ID, LAST_UPDATED_BY_ID,
    CREATED_AT, LAST_UPDATED_AT
)
VALUES (
           10003, 'Emergency Event 1', 'Test emergency event', 'EMERGENCY',
           'user-2-uuid', 'user-2-uuid',
           NOW(), NOW()
       );

-- Archived Emergency
INSERT INTO EVENT (
    ID, TITLE, DESCRIPTION, EVENT_TYPE,
    CREATED_BY_ID, LAST_UPDATED_BY_ID,
    CREATED_AT, LAST_UPDATED_AT,
    ARCHIVED_AT
)
VALUES (
           10004, 'Emergency Event 2', 'Archived emergency event', 'EMERGENCY',
           'user-2-uuid', 'user-2-uuid',
           NOW(), NOW(),
           NOW()
       );

-- --------------------------
-- Event Tickets
-- --------------------------
INSERT INTO EVENT_TICKET (
    ID, EVENT_ID, EVENT_DATE, TICKET_NAME,
    DECISIONS, PROGNOSTICS,
    CREATED_BY_ID, LAST_UPDATED_BY_ID,
    CREATED_AT, LAST_UPDATED_AT
)
VALUES (
           10001, 10001, NOW(), 'Ticket 1',
           'Decisions 1', 'Prognostics 1',
           'user-1-uuid', 'user-1-uuid',
           NOW(), NOW()
       );

INSERT INTO EVENT_TICKET (
    ID, EVENT_ID, EVENT_DATE, TICKET_NAME,
    DECISIONS, PROGNOSTICS,
    CREATED_BY_ID, LAST_UPDATED_BY_ID,
    CREATED_AT, LAST_UPDATED_AT
)
VALUES (
           10002, 10002, NOW(), 'Ticket 2',
           'Decisions 2', 'Prognostics 2',
           'user-1-uuid', 'user-1-uuid',
           NOW(), NOW()
       );

INSERT INTO EVENT_TICKET (
    ID, EVENT_ID, EVENT_DATE, TICKET_NAME,
    DECISIONS, PROGNOSTICS,
    CREATED_BY_ID, LAST_UPDATED_BY_ID,
    CREATED_AT, LAST_UPDATED_AT
)
VALUES (
           10003, 10003, NOW(), 'Ticket 3',
           'Decisions 3', 'Prognostics 3',
           'user-2-uuid', 'user-2-uuid',
           NOW(), NOW()
       );

INSERT INTO EVENT_TICKET (
    ID, EVENT_ID, EVENT_DATE, TICKET_NAME,
    DECISIONS, PROGNOSTICS,
    CREATED_BY_ID, LAST_UPDATED_BY_ID,
    CREATED_AT, LAST_UPDATED_AT
)
VALUES (
           10004, 10004, NOW(), 'Ticket 4',
           'Decisions 4', 'Prognostics 4',
           'user-2-uuid', 'user-2-uuid',
           NOW(), NOW()
       );