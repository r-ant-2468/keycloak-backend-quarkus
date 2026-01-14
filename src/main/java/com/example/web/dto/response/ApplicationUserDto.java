package com.example.web.dto.response;

import com.example.domain.ApplicationUser;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.time.Instant;

public class ApplicationUserDto {

    public String id;
    public String email;

    public ApplicationUserDto(ApplicationUser applicationUser) {
        this.id = applicationUser.getId();
        this.email = applicationUser.getEmail();
    }
}
