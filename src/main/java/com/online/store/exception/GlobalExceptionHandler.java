package com.online.store.exception;

import com.online.store.exception.cart.CartIsEmptyException;
import com.online.store.exception.cart.CartItemNotFoundException;
import com.online.store.exception.order.OrderNotFoundException;
import com.online.store.exception.product.ProductNotFoundException;
import com.online.store.exception.user.AuthenticationUserException;
import com.online.store.exception.user.UserExistsException;
import com.online.store.exception.user.UserNotFoundException;
import com.online.store.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerProductNotFound(ProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handlerValidationException(ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<ErrorResponse> handlerUserExists(UserExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(
                        LocalDateTime.now(),
                        HttpStatus.CONFLICT,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(AuthenticationUserException.class)
    public ResponseEntity<ErrorResponse> handlerAuthenticationUser(AuthenticationUserException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handlerMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.of(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                errorMessage
        );

        return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handlerWebExchangeBind(WebExchangeBindException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST,
                        errorMessage
                ));
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerCartItemNotFound(CartItemNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(CartIsEmptyException.class)
    public ResponseEntity<ErrorResponse> handlerCartIsEmpty(CartIsEmptyException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerOrderNotFound(OrderNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND,
                        e.getMessage()
                ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND,
                        e.getMessage()
                ));
    }
}
