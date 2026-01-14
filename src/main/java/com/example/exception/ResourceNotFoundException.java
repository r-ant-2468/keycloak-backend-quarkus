package com.example.exception;

public class ResourceNotFoundException extends DomainResourceException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
