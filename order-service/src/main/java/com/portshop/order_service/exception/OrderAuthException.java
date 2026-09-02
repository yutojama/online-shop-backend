package com.portshop.order_service.exception;

public class OrderAuthException extends RuntimeException {
    public OrderAuthException(String message) {
        super(message);
    }
}
