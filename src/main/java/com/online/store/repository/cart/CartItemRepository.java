package com.online.store.repository.cart;

import com.online.store.model.cart.CartItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CartItemRepository extends ReactiveCrudRepository<CartItem, UUID> {
    Flux<CartItem> findAllByCartUuid(UUID cartUuid);
    Mono<Void> deleteAllByCartUuid(UUID cartUuid);
}
