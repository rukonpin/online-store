package com.online.store.exception.cart;

import java.util.UUID;

public class CartIsEmptyException extends RuntimeException {
    public CartIsEmptyException(UUID userUuid) {
        super("Cart is empty for user with" + userUuid);
    }
}
