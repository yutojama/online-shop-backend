package com.portshop.shipping_service.exception;

public class ShippingExistingException extends RuntimeException {
    public ShippingExistingException(String message) {
        super(message);
    }
}
