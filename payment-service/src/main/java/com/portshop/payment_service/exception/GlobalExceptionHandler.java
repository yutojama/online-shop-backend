package com.portshop.payment_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler{

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        logger.warn(e.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("Error", "Invalid request value");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<?> handlePaymentNotFoundException(PaymentNotFoundException e){
        logger.warn("Payment not found {}", e.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("Payment not found", e.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PaymentExistingException.class)
    public ResponseEntity<?> handlePaymentExistingException(PaymentExistingException e){
        logger.warn("Payment already existing {}", e.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("Payment already existing", e.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PaymentAuthenticationException.class)
    public ResponseEntity<?> handlePaymentAuthenticationException(PaymentAuthenticationException e){
        logger.warn("Authentication failed {}", e.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("Authentication failed", e.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }
}
