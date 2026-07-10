package com.online.store.exception.user;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userUuid) {
        super("User with this " + userUuid + " not found");
    }
}
