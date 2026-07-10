package com.online.store.payment.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(UUID userUuid, BigDecimal requested, BigDecimal available) {
        super("Insufficient funds for user " + userUuid + ": requested " + requested + ", available " + available);
    }
}
