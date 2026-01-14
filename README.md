# keycloak-backend-quarkus

This repository gives an example of RBAC protected endpoints, with user information and roles loaded via a keycloak connection.

Devservices are configured to run Keycloak and the database when running locally in containers.

## Running in dev mode

### Requirements:
- JDK 25+
- Docker


### Run command:

```shell script
./mvnw quarkus:dev
```

This command will leave Quarkus running in the foreground listening on port 8080.

Now open [OIDC dev UI](http://localhost:8080/api/q/dev-ui/quarkus-oidc/keycloak-provider). You will be asked to login into a _Single Page Application_. You can login as any of the users defined in the [users section](#example-users). After connecting, you can visit the [swagger ui](http://localhost:8080/api/q/swagger-ui/) to test endpoints of the application.

## Functionality

This application offers REST API endpoints based on `Events` and related `Tickets`:

* GET  `/api/events` Get events 
* POST `/api/events` Create event 
* PUT  `/api/events/{id}` Edit event 
* GET  `/api/events/{id}/tickets` Get tickets for an event 
* POST `/api/events/{id}/tickets` Create a ticket for an event
* PUT  `/api/events/{id}/tickets/{ticket_id}` Edit a ticket associated to an event 

Events can be of type `Emergency` or `Maintenance`

## Access

Access to resources based on the different event types are managed by roles defined in keycloak.
Keycloak configuration is defined in the following files:

- [src/main/resources/dev-realm.json](src/main/resources/dev-realm.json) for `dev` mode
- [src/test/resources/test-realm.json](src/test/resources/test-realm.json) for `test` mode

### Role hierarchy

The following hierarchy describes the roles and their inheritance:

```
ROLE_ADMIN
├── ROLE_EMERGENCY_RW - Create/Edit Emergency events and related tickets 
│   └── ROLE_EMERGENCY_RO - View Emergency events and related tickets
└── ROLE_MAINTENANCE_RW - Create/Edit maintenance events and related tickets
    └── ROLE_MAINTENANCE_RO - View maintenance events and related tickets
```

### Example users

The config also defines the following users for testing access with different roles:


- **Username:** `admin`  
  **Password:** `admin`  
  **Role:** `ROLE_ADMIN`

- **Username:** `emergencyuserrw`  
  **Password:** `emergencyuserrw`  
  **Role:** `ROLE_EMERGENCY_RW`

- **Username:** `emergencyuserro`  
  **Password:** `emergencyuserro`  
  **Role:** `ROLE_EMERGENCY_RO`

- **Username:** `maintenanceuserrw`  
  **Password:** `maintenanceuserrw`  
  **Role:** `ROLE_MAINTENANCE_RW`

- **Username:** `maintenanceuserro`  
  **Password:** `maintenanceuserro`  
  **Role:** `ROLE_MAINTENANCE_RO`


## Running Tests

Integration tests have focus on access given to each role.

The tests can be run using the following command:

```shell script
./mvnw test
```

- A `h2` database is used for testing to imitate a postgres db whilst occupying minimal memory.
- Direct access grants are enabled (`"directAccessGrantsEnabled": true`) within the [test realm](src/test/resources/test-realm.json) to allow for programmatic login within the tests. _This config deactivated in dev mode and is not advised for production as it is less secure_.

See full test configuration [here](src/test/resources/application.yml)


## Deployment

This is an example application. For production deployment, devservices cannot be used, the following servers will need to be setup:

- Keycloak
- postgreSQL

These servers will need to be referenced in the [application.yaml](src/main/resources/application.yml) config file.


## Detailed keycloak configuration notes

For understanding the setup of keycloak when run in dev mode, we need to inspect the json config file: [src/main/resources/dev-realm.json](src/main/resources/dev-realm.json).

### Client configuration
- `"publicClient": true` within a context of a spa application, a client secret is not safe within a browser. Therefore no secret is used and we define the client as public.
- `"protocol": "openid-connect"` defines the client connection using the standard OIDC protocol (id tokens, access tokens, refresh tokens etc...).

The client also configures `defaultClientScopes` which references client scopes defined in the config under the `clientScopes` array. 

Client scopes contain mapping (`protocolMappers`) configuration of how certain information from keycloak can be accessed by the client. 

A couple of key client scopes can be noted in the `defaultClientScopes` key:
- `basic` client scope, containing a protocol mapper which references another mapper `"protocolMapper": "oidc-sub-mapper"`. This mapper allows the client to access the ID of the logged in user, which is also used as the Id of the user in the application database.
- `roles` client scope, containing a protocol mapper for the client roles `"claim.name": "resource_access.${client_id}.roles"`. This is essential so that the application can read roles associated with the logged in user to configure access.


### Roles
- Client roles are configured in the `roles` array, defining the name, client and any composite roles (for the role hierarchy)

### Users
- Users created during keycloak startup are configured in the `users` array, containing an array of users with personal information, credentials (`username` and `password`) and roles (`clientRoles`).

## AI Usage
No part of this repository may be used to train machine learning models or artificial intelligence without express written permission.