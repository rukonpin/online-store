package com.online.store.controller.order.api;

import com.online.store.dto.order.OrderDto;
import com.online.store.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderRestController {

    private final OrderService orderService;

    @PostMapping
    public Mono<ResponseEntity<OrderDto>> createOrder(WebSession session) {
        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return orderService.createOrder(userUuid)
                .flatMap(orderService::toDtoWithProducts)
                .map(orderDto -> ResponseEntity.status(HttpStatus.CREATED).body(orderDto));
    }

    @GetMapping("/{orderUuid}")
    public Mono<ResponseEntity<OrderDto>> getOrder(@PathVariable UUID orderUuid, WebSession session) {
        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return orderService.getOrder(orderUuid, userUuid)
                .flatMap(orderService::toDtoWithProducts)
                .map(ResponseEntity::ok);
    }
}
