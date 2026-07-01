package com.online.store.controller.cart.api;

import com.online.store.dto.cart.CartDto;
import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.cart.UpdateItemQuantityDto;
import com.online.store.model.cart.Cart;
import com.online.store.model.cart.CartItem;
import com.online.store.model.product.Product;
import com.online.store.service.cart.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebFluxTest(CartRestController.class)
class CartRestControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartService cartService;

    private UUID userUuid;
    private UUID itemUuid;
    private UUID productUuid;
    private UUID cartUuid;
    private Cart mockCart;
    private CartDto mockCartDto;
    private CartItemDto itemResponseDto;

    @BeforeEach
    void setUp() {
        userUuid = UUID.randomUUID();
        itemUuid = UUID.randomUUID();
        productUuid = UUID.randomUUID();
        cartUuid = UUID.randomUUID();

        Product mockProduct = Product.builder()
                .productUuid(productUuid)
                .build();

        CartItem cartItem = CartItem.builder()
                .itemUuid(itemUuid)
                .productUuid(productUuid)
                .quantity(1)
                .build();

        List<CartItem> itemsInCart = new ArrayList<>();
        itemsInCart.add(cartItem);

        mockCart = Cart.builder()
                .cartUuid(cartUuid)
                .userUuid(userUuid)
                .items(itemsInCart)
                .build();

        itemResponseDto = CartItemDto.builder()
                .uuid(itemUuid)
                .productUuid(productUuid)
                .quantity(1)
                .totalPrice(BigDecimal.TEN)
                .build();

        mockCartDto = CartDto.builder()
                .uuid(cartUuid)
                .userUuid(userUuid)
                .items(List.of(itemResponseDto))
                .build();
    }

    @Test
    @DisplayName("GET /api/cart - should return 401 when user is not authenticated")
    void getCart_WhenNotAuthenticated_ReturnsUnauthorized() {
        webTestClient.get().uri("/api/cart")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("POST /api/cart/items - should return 401 when user is not authenticated")
    void addItemToCart_WhenNotAuthenticated_ReturnsUnauthorized() {
        CartItemDto requestDto = CartItemDto.builder()
                .productUuid(productUuid)
                .quantity(1)
                .build();

        webTestClient.post().uri("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("PUT /api/cart/items/{itemUuid} - should return 401 when user is not authenticated")
    void updateItem_WhenNotAuthenticated_ReturnsUnauthorized() {
        UpdateItemQuantityDto quantityDto = new UpdateItemQuantityDto();
        quantityDto.setQuantity(2);

        webTestClient.put().uri("/api/cart/items/{itemUuid}", itemUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(quantityDto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("DELETE /api/cart/items/{itemUuid} - should return 401 when user is not authenticated")
    void deleteItem_WhenNotAuthenticated_ReturnsUnauthorized() {
        webTestClient.delete().uri("/api/cart/items/{itemUuid}", itemUuid)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("DELETE /api/cart - should return 401 when user is not authenticated")
    void cleanCart_WhenNotAuthenticated_ReturnsUnauthorized() {
        webTestClient.delete().uri("/api/cart")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}