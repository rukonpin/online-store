package com.online.store.service.order;

import com.online.store.dto.order.OrderDto;
import com.online.store.model.order.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderService {
    Mono<Order> createOrder(UUID userUuid);
    Mono<Order> getOrder(UUID orderUuid, UUID userUuid);
    Flux<Order> getAllOrders(UUID userUuid);
    Mono<OrderDto> toDtoWithProducts(Order order);
}
