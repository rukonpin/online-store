package com.online.store.exception.cart;

import java.util.UUID;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(UUID itemUuid) {
        super("Could not find item with uuid " + itemUuid);
    }
}
