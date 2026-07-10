package com.online.store.controller.cart.api;

import com.online.store.dto.cart.CartDto;
import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.cart.UpdateItemQuantityDto;
import com.online.store.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartRestController {

    private final CartService cartService;

    @GetMapping
    public Mono<ResponseEntity<CartDto>> getCart(WebSession session) {
        UUID userUuid = session.getAttribute("user_id");

        if (userUuid == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return cartService.getOrCreateCart(userUuid)
                .flatMap(cartService::toDtoWithProducts)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/items")
    public Mono<ResponseEntity<CartDto>> addItemToCart(
            WebSession session,
            @Valid @RequestBody CartItemDto itemDto) {

        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return cartService.addItem(userUuid, itemDto)
                .flatMap(cartService::toDtoWithProducts)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/items/{itemUuid}")
    public Mono<ResponseEntity<CartDto>> updateItem(
            WebSession session,
            @PathVariable UUID itemUuid,
            @Valid @RequestBody UpdateItemQuantityDto quantityDto) {

        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return cartService.updateItem(userUuid, itemUuid, quantityDto)
                .flatMap(cartService::toDtoWithProducts)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/items/{itemUuid}")
    public Mono<ResponseEntity<CartDto>> deleteItem(
            WebSession session,
            @PathVariable UUID itemUuid) {

        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return cartService.removeItem(userUuid, itemUuid)
                .flatMap(cartService::toDtoWithProducts)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping
    public Mono<ResponseEntity<CartDto>> cleanCart(WebSession session) {
        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return cartService.cleanCart(userUuid)
                .flatMap(cartService::toDtoWithProducts)
                .map(ResponseEntity::ok);
    }
}
