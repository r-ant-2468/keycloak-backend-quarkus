package com.example.exception;

public class ForbiddenActionException extends DomainResourceException {
    public ForbiddenActionException(String message) {
        super(message);
    }
}
