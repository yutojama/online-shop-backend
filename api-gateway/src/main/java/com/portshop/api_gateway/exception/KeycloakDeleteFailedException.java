package com.portshop.api_gateway.exception;

public class KeycloakDeleteFailedException extends RuntimeException {
    public KeycloakDeleteFailedException(String message) {
        super(message);
    }
}
