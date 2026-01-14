package com.example.service;

import com.example.domain.ApplicationUser;
import com.example.exception.ResourceNotFoundException;
import com.example.persistence.repository.ApplicationUserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsible for managing application users.
 */
@ApplicationScoped
public class ApplicationUserService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationUserService.class);

    private final ApplicationUserRepository applicationUserRepository;

    public ApplicationUserService(
            final ApplicationUserRepository applicationUserRepository
    ) {
        this.applicationUserRepository = applicationUserRepository;
    }

    public ApplicationUser getById(String id) {
        return this.applicationUserRepository.getById(id).orElseThrow(
                () -> new ResourceNotFoundException("Application user with ID: " + id + " does not exist")
        );
    }

    @Transactional
    public ApplicationUser create(ApplicationUser applicationUser) {
        this.applicationUserRepository.create(applicationUser);
        return applicationUser;
    }

    @Transactional
    public ApplicationUser update(ApplicationUser applicationUser) {
        return this.applicationUserRepository.update(applicationUser);
    }
}
