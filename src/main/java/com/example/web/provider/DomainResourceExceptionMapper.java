package com.example.web.provider;

import com.example.exception.DomainResourceException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;

/**
 * Intercepts DomainResourceExceptions to provide a uniform API response.
 * 
 * By returning a 404 for all subtypes of DomainResourceException, we prevent 
 * callers from determining if a resource is missing or simply restricted.
 */
@Provider
public class DomainResourceExceptionMapper implements ExceptionMapper<DomainResourceException> {

    private static final Logger LOG = LoggerFactory.getLogger(DomainResourceExceptionMapper.class);

    @Override
    public Response toResponse(DomainResourceException exception) {
        LOG.warn("Resource access restricted or missing: {} - {}", 
                exception.getClass().getSimpleName(), exception.getMessage());

        Response.Status status = Response.Status.NOT_FOUND;
        
        Map<String, Object> body = Map.of(
                "status", status.getStatusCode(),
                "error", "Not Found",
                "message", "The requested resource is not accessible",
                "timestamp", Instant.now().toString()
        );

        return Response.status(status).entity(body).build();
    }
}
