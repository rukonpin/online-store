package com.online.store.repository.order;

import com.online.store.model.order.OrderItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, UUID> {
    Flux<OrderItem> findAllByOrderUuid(UUID orderUuid);
}
