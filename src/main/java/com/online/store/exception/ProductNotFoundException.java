package com.online.store.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID uuid) {
        super("Product with this " + uuid + " not found");
    }
}
