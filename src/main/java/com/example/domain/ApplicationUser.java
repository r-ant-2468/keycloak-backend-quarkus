package com.example.domain;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "APPLICATION_USER")
public class ApplicationUser {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_EMERGENCY_RW = "ROLE_EMERGENCY_RW";
    public static final String ROLE_MAINTENANCE_RW = "ROLE_MAINTENANCE_RW";
    public static final String ROLE_EMERGENCY_RO = "ROLE_EMERGENCY_RO";
    public static final String ROLE_MAINTENANCE_RO = "ROLE_MAINTENANCE_RO";
    public static final List<String> AVAILABLE_ROLES =
            List.of(
                    ROLE_ADMIN,
                    ROLE_EMERGENCY_RW,
                    ROLE_MAINTENANCE_RW,
                    ROLE_EMERGENCY_RO,
                    ROLE_MAINTENANCE_RO
            );

    public ApplicationUser() {
    }

    public ApplicationUser(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Id
    @Column(name = "ID", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String keycloakId) {
        this.id = keycloakId;
    }

    public boolean requiresDbUpdate(String email, String newFirstName, String newLastName) {
        return
                Objects.equals(this.getEmail(), email) &&
                        !Objects.equals(this.getFirstName(), newFirstName) ||
                        !Objects.equals(this.getLastName(), newLastName);
    }

}
