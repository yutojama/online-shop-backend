package com.portshop.user_service.exception;

public class KeycloakDeleteFailedException extends RuntimeException {
    public KeycloakDeleteFailedException(String message) {
        super(message);
    }
}
