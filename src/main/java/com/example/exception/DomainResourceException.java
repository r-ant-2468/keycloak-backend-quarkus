package com.example.exception;

/**
 * Base class for domain exceptions related to resource access and availability.
 */
public abstract class DomainResourceException extends RuntimeException {
    protected DomainResourceException(String message) {
        super(message);
    }
}
