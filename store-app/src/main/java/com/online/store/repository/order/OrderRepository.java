package com.online.store.repository.order;

import com.online.store.model.order.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface OrderRepository extends ReactiveCrudRepository<Order, UUID> {
    Flux<Order> findAllByUserUuidOrderByCreatedAtDesc(UUID userUuid);
}
