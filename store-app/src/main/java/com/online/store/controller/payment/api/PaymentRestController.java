package com.online.store.controller.payment.api;

import com.online.store.service.payment.PaymentClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentRestController {

    private final PaymentClientService paymentClientService;

    @GetMapping("/balance")
    public Mono<ResponseEntity<Map<String, Object>>> getBalance(WebSession session) {
        UUID userUuid = (UUID) session.getAttribute("user_id");
        if (userUuid == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
        return paymentClientService.getBalance(userUuid)
                .map(balance -> ResponseEntity.ok(Map.<String, Object>of(
                        "balance",
                        balance,
                        "available",
                        true)))
                .switchIfEmpty(Mono.just(ResponseEntity.ok(Map.of("available", false))));
    }
}
