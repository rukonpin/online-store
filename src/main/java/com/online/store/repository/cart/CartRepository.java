package com.online.store.repository.cart;

import com.online.store.model.cart.Cart;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CartRepository extends ReactiveCrudRepository<Cart, UUID> {
    Mono<Cart> findByUserUuid(UUID userUuid);
}
