package com.online.store.payment.delegate;

import com.online.store.payment.api.PaymentApiDelegate;
import com.online.store.payment.exception.InsufficientFundsException;
import com.online.store.payment.model.ChargePaymentRequest;
import com.online.store.payment.model.GetBalance200Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentApiDelegateImpl implements PaymentApiDelegate {

    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(500000);

    private final Map<UUID, BigDecimal> balances = new ConcurrentHashMap<>();

    @Override
    public Mono<ResponseEntity<GetBalance200Response>> chargePayment(
            Mono<ChargePaymentRequest> chargePaymentRequest,
            ServerWebExchange exchange) {
        return chargePaymentRequest.flatMap(request -> {
            UUID userUuid = request.getUserUuid();
            BigDecimal amount = request.getAmount();

            BigDecimal current = balances.computeIfAbsent(userUuid, u -> INITIAL_BALANCE);

            if (current.compareTo(amount) < 0) {
                return Mono.error(new InsufficientFundsException(userUuid, current, amount));
            }

            BigDecimal updated = current.subtract(amount);
            balances.put(userUuid, updated);

            GetBalance200Response response = new GetBalance200Response().balance(updated);
            return Mono.just(ResponseEntity.ok(response));
        });
    }

    @Override
    public Mono<ResponseEntity<GetBalance200Response>> getBalance(UUID userUuid, ServerWebExchange exchange) {
        BigDecimal balance = balances.computeIfAbsent(userUuid, u -> INITIAL_BALANCE);
        GetBalance200Response response = new GetBalance200Response().balance(balance);
        return Mono.just(ResponseEntity.ok(response));
    }
}
