package com.portshop.api_gateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(KeycloakCreateUserException.class)
    public ResponseEntity<?> handleKeycloakCreateUserException(KeycloakCreateUserException e){
        Map<String, String> errors = new HashMap<>();
        errors.put("error ", e.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(KeycloakDeleteFailedException.class)
    public ResponseEntity<?> handleKeycloakDeleteFailedException(KeycloakDeleteFailedException e){
        Map<String, String> errors = new HashMap<>();
        errors.put("error ", e.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(InvalidKeycloakCredentialsException.class)
    public ResponseEntity<?> handleInvalidKeycloakCredentialsException(InvalidKeycloakCredentialsException e){
        Map<String, String> errors = new HashMap<>();
        errors.put("error ", e.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(AuthSessionNotFoundException.class)
    public ResponseEntity<?> handleKeycloakUserNotFoundException(AuthSessionNotFoundException e){
        Map<String, String> errors = new HashMap<>();
        errors.put("error ", e.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

}
