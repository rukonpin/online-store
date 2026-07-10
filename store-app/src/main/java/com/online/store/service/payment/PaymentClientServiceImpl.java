package com.online.store.service.payment;

import com.online.store.exception.payment.InsufficientFundsException;
import com.online.store.exception.payment.PaymentServiceUnavailableException;
import com.online.store.payment.client.api.PaymentApi;
import com.online.store.payment.client.model.ChargePaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentClientServiceImpl implements PaymentClientService {

    private final PaymentApi paymentApi;

    @Override
    public Mono<BigDecimal> getBalance(UUID userUuid) {
        return paymentApi.getBalance(userUuid)
                .map(response -> response.getBalance())
                .onErrorResume(e -> {
                    return Mono.empty();
                });
    }

    @Override
    public Mono<BigDecimal> chargePayment(UUID userUuid, UUID orderUuid, BigDecimal amount) {
        ChargePaymentRequest request = new ChargePaymentRequest()
                .userUuid(userUuid)
                .orderUuid(orderUuid)
                .amount(amount);

        return paymentApi.chargePayment(request)
                .map(response -> response.getBalance())
                .onErrorMap(WebClientResponseException.class, e -> mapChargeError(e, userUuid))
                .onErrorMap(e -> !(e instanceof InsufficientFundsException), e -> {
                    return new PaymentServiceUnavailableException("Сервис платежей недоступен");
                });
    }

    private RuntimeException mapChargeError(WebClientResponseException e, UUID userUuid) {
        if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
            return new InsufficientFundsException("Недостаточно средств для оплаты заказа");
        }
        return new PaymentServiceUnavailableException("Сервис платежей недоступен");
    }
}
