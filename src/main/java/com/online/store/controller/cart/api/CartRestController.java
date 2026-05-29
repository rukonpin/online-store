package com.online.store.controller.cart.api;

import com.online.store.dto.cart.CartDto;
import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.cart.UpdateItemQuantityDto;
import com.online.store.mapper.cart.CartMapper;
import com.online.store.model.cart.Cart;
import com.online.store.service.cart.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartRestController {

    private final CartService cartService;
    private final CartMapper cartMapper;

    @GetMapping
    public ResponseEntity<CartDto> getCart(HttpSession session) {
        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Cart cart = cartService.getOrCreateCart(userUuid);
        CartDto cartDto = cartMapper.toDto(cart);

        return ResponseEntity.ok(cartDto);
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItemToCart(
            HttpSession session,
            @Valid @RequestBody CartItemDto itemDto) {

        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Cart cart = cartService.addItem(userUuid, itemDto);
        CartDto cartDto = cartMapper.toDto(cart);

        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/items/{itemUuid}")
    public ResponseEntity<CartDto> updateItem(
            HttpSession session,
            @PathVariable UUID itemUuid,
            @Valid @RequestBody UpdateItemQuantityDto quantityDto) {

        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Cart cart = cartService.updateItem(userUuid, itemUuid, quantityDto);
        CartDto cartDto = cartMapper.toDto(cart);

        return ResponseEntity.ok(cartDto);
    }

    @DeleteMapping("/items/{itemUuid}")
    public ResponseEntity<CartDto> deleteItem(
            HttpSession session,
            @PathVariable UUID itemUuid) {

        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Cart cart = cartService.removeItem(userUuid, itemUuid);
        CartDto cartDto = cartMapper.toDto(cart);

        return ResponseEntity.ok(cartDto);
    }

    @DeleteMapping
    public ResponseEntity<CartDto> cleanCart(HttpSession session) {
        UUID userUuid = (UUID) session.getAttribute("user_id");
        if (userUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Cart cart = cartService.cleanCart(userUuid);
        CartDto cartDto = cartMapper.toDto(cart);
        return ResponseEntity.ok(cartDto);
    }
}
