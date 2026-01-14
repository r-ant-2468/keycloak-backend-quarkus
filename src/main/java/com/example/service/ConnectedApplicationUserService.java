package com.example.service;

import com.example.domain.ApplicationUser;
import com.example.exception.ResourceNotFoundException;
import io.quarkus.oidc.UserInfo;
import io.quarkus.security.AuthenticationFailedException;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service to retrieve and synchronize the currently authenticated ApplicationUser.
 * This class bridge the gap between OIDC security identity and the database user entity.
 */
@ApplicationScoped
public class ConnectedApplicationUserService {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectedApplicationUserService.class);

    private final SecurityIdentity securityIdentity;
    private final ApplicationUserService applicationUserService;

    public ConnectedApplicationUserService(SecurityIdentity securityIdentity,
                                           ApplicationUserService applicationUserService) {
        this.securityIdentity = securityIdentity;
        this.applicationUserService = applicationUserService;
    }

    private UserInfo getUserInfo() {
        return this.securityIdentity.getAttribute("userinfo");
    }

    /**
     * Returns the Keycloak "sub" claim (unique user ID) of the currently logged-in user.
     */
    private String getKeycloakId() {
        UserInfo loggedInUserInfo = this.getUserInfo();
        if (loggedInUserInfo != null) {
            String keycloakId = loggedInUserInfo.getString("sub");
            if (keycloakId != null) {
                return keycloakId;
            }
            throw new RuntimeException("Keycloak id cannot be extracted from token");
        }
        throw new RuntimeException("User info cannot be extracted from token");
    }

    /**
     * Returns the ApplicationUser entity for the currently logged-in user.
     *
     * @throws ResourceNotFoundException if the user does not exist in the database.
     */
    public ApplicationUser getApplicationUser() {
        String keycloakId = getKeycloakId();
        return this.applicationUserService.getById(keycloakId);
    }

    public boolean hasRole(String role) {
        return this.securityIdentity.hasRole(role);
    }

    @Transactional
    public void updateDbUserFromToken() {

        if (securityIdentity.isAnonymous()) {
            return;
        }

        UserInfo userInfo = this.securityIdentity.getAttribute("userinfo");
        if (userInfo == null) {
            throw new AuthenticationFailedException("UserInfo not available");
        }
        String keycloakId = null;
        try {
            keycloakId = this.getKeycloakId();
        } catch (RuntimeException e) {
            throw new AuthenticationFailedException("Keycloak ID not available");
        }
        String keycloakEmail = userInfo.getEmail();
        String keycloakFirstName = userInfo.getString("given_name");
        String keycloakLastName = userInfo.getFamilyName();

        if (keycloakEmail == null) {
            throw new AuthenticationFailedException("Keycloak email is null for account token");
        }
        if (keycloakFirstName == null) {
            throw new AuthenticationFailedException("Keycloak first name is null for account token");
        }
        if (keycloakLastName == null) {
            throw new AuthenticationFailedException("Keycloak last name is null for account token");
        }

        try {
            ApplicationUser savedApplicationUser = this.applicationUserService.getById(keycloakId);

            boolean dbUserRequiresUpdate = savedApplicationUser.requiresDbUpdate(keycloakEmail, keycloakFirstName, keycloakLastName);
            if (dbUserRequiresUpdate) {
                savedApplicationUser.setFirstName(keycloakFirstName);
                savedApplicationUser.setLastName(keycloakLastName);
                this.applicationUserService.update(savedApplicationUser);
                LOG.info("Updated user from token with email {}", keycloakEmail);
            } else {
                LOG.debug("No update required for user from token with email {}", keycloakEmail);
            }
        } catch (ResourceNotFoundException notFoundException) {
            // Create new user if none in current db
            this.applicationUserService.create(new ApplicationUser(keycloakId, keycloakFirstName, keycloakLastName, keycloakEmail));
            LOG.info("Inserted user from token for email {}", keycloakEmail);
        }

    }


}