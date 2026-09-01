package com.portshop.payment_service.exception;

public class PaymentAuthenticationException extends RuntimeException {
    public PaymentAuthenticationException(String message) {
        super(message);
    }
}
