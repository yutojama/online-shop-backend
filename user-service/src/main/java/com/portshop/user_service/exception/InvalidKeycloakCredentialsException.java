package com.portshop.user_service.exception;

public class InvalidKeycloakCredentialsException extends RuntimeException {
    public InvalidKeycloakCredentialsException(String message) {
        super(message);
    }
}
