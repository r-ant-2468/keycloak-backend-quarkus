package com.example.security;

import com.example.domain.ApplicationUser;
import com.example.domain.enums.EventType;
import com.example.service.ConnectedApplicationUserService;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Utility service for evaluating business-level security rules based on user roles.
 */
@ApplicationScoped
public class RoleSecurity {

    private static final Logger LOG = LoggerFactory.getLogger(RoleSecurity.class);
    private final ConnectedApplicationUserService connectedApplicationUserService;

    public RoleSecurity(ConnectedApplicationUserService connectedApplicationUserService) {
        this.connectedApplicationUserService = connectedApplicationUserService;
    }

    public boolean canViewEventType(EventType eventType) {
        if (eventType == null) {
            return connectedUserHasAllRoles(List.of(ApplicationUser.ROLE_MAINTENANCE_RO, ApplicationUser.ROLE_EMERGENCY_RO));
        }

        return switch (eventType) {
            case EventType.EMERGENCY -> connectedUserHasRole(ApplicationUser.ROLE_EMERGENCY_RO);
            case EventType.MAINTENANCE -> connectedUserHasRole(ApplicationUser.ROLE_MAINTENANCE_RO);
        };
    }

    public boolean canCreateOrEditEvents(EventType eventType) {
        if (eventType == null) {
            return connectedUserHasAllRoles(List.of(ApplicationUser.ROLE_MAINTENANCE_RW, ApplicationUser.ROLE_EMERGENCY_RW));
        }
        return switch (eventType) {
            case EventType.EMERGENCY -> connectedUserHasRole(ApplicationUser.ROLE_EMERGENCY_RW);
            case EventType.MAINTENANCE -> connectedUserHasRole(ApplicationUser.ROLE_MAINTENANCE_RW);
        };
    }

    public boolean connectedUserHasAllRoles(List<String> roleList) {
        return roleList.stream().allMatch(this::connectedUserHasRole);
    }

    public boolean connectedUserHasRole(String role) {
        if (isRoleValid(role)) {
            return this.connectedApplicationUserService.hasRole(role);
        }
        LOG.warn("{} role to check is not recognized", role);
        return false;
    }

    private boolean isRoleValid(String role) {
        return ApplicationUser.AVAILABLE_ROLES.contains(role);
    }

}
