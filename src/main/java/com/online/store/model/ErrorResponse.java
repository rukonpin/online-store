package com.online.store.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final HttpStatus status;
    private final String message;

    public static ErrorResponse of(LocalDateTime timestamp, HttpStatus status, String message) {
        return new ErrorResponse(timestamp, status, message);
    }
}
