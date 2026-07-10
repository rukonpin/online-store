package com.online.store.service.payment;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentClientService {
    Mono<BigDecimal> getBalance(UUID userUuid);
    Mono<BigDecimal> chargePayment(UUID userUuid, UUID orderUuid, BigDecimal amount);
}
