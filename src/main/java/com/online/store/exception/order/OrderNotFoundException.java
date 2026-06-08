package com.online.store.exception.order;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID orderUuid) {
        super("Order with this " + orderUuid + " not found");
    }
}
