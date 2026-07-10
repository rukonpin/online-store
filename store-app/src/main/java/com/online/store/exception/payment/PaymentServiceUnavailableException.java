package com.online.store.exception.payment;

public class PaymentServiceUnavailableException extends RuntimeException {
    public PaymentServiceUnavailableException(String message) {
        super(message);
    }
}
