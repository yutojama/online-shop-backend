package com.portshop.api_gateway.exception;

public class InvalidKeycloakCredentialsException extends RuntimeException {
    public InvalidKeycloakCredentialsException(String message) {
        super(message);
    }
}
