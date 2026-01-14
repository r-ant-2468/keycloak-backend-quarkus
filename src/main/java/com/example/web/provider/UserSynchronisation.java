package com.example.web.provider;

import com.example.service.ConnectedApplicationUserService;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;


/**
 * {@code UserSynchronisation} synchronizes authenticated Keycloak user information
 * with the local application database.
 *
 * <p>This filter runs on every authenticated HTTP request after OIDC
 * authentication has completed</p>
 *
 */
@Provider
@Priority(Priorities.AUTHENTICATION + 1)
public class UserSynchronisation implements ContainerRequestFilter {

    private final ConnectedApplicationUserService connectedApplicationUserService;

    public UserSynchronisation(ConnectedApplicationUserService connectedApplicationUserService) {
        this.connectedApplicationUserService = connectedApplicationUserService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        this.connectedApplicationUserService.updateDbUserFromToken();
    }
}
