package com.online.store.service.cart;

import com.online.store.dto.cart.CartDto;
import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.cart.UpdateItemQuantityDto;
import com.online.store.model.cart.Cart;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CartService {
    Mono<Cart> getOrCreateCart(UUID userUuid);
    Mono<Cart> addItem(UUID userUuid, CartItemDto itemDto);
    Mono<Cart> updateItem(UUID userUuid, UUID itemUuid, UpdateItemQuantityDto quantityDto);
    Mono<Cart> removeItem(UUID userUuid, UUID itemUuid);
    Mono<Cart> cleanCart(UUID userUuid);
    Mono<CartDto> toDtoWithProducts(Cart cart);
}
