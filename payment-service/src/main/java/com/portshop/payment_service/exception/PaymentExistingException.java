package com.portshop.payment_service.exception;

public class PaymentExistingException extends RuntimeException {
    public PaymentExistingException(String message) {
        super(message);
    }
}
