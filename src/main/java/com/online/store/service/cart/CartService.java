package com.online.store.service.cart;

import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.cart.UpdateItemQuantityDto;
import com.online.store.model.cart.Cart;

import java.util.UUID;

public interface CartService {

    Cart getOrCreateCart(UUID userUuid);
    Cart addItem(UUID userUuid, CartItemDto itemDto);
    Cart updateItem(UUID userUuid, UUID itemUuid, UpdateItemQuantityDto quantityDto);
    Cart removeItem(UUID userUuid, UUID itemUuid);
    Cart cleanCart(UUID userUuid);
}
