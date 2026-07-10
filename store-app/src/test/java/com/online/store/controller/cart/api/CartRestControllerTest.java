package com.online.store.controller.cart.api;

import com.online.store.dto.cart.CartDto;
import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.cart.UpdateItemQuantityDto;
import com.online.store.model.cart.Cart;
import com.online.store.model.cart.CartItem;
import com.online.store.service.cart.CartService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClientConfigurer;
import reactor.core.publisher.Mono;
import org.springframework.web.server.adapter.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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

    private WebTestClientConfigurer sessionUser(UUID userId) {
        return (builder, httpHandlerBuilder, connector) -> {
            Assertions.assertNotNull(httpHandlerBuilder);
            httpHandlerBuilder.filter((exchange, chain) ->
                    exchange.getSession()
                            .doOnNext(session -> session.getAttributes().put("user_id", userId))
                            .then(chain.filter(exchange))
            );
        };
    }

    @BeforeEach
    void setUp() {
        userUuid = UUID.randomUUID();
        itemUuid = UUID.randomUUID();
        productUuid = UUID.randomUUID();
        cartUuid = UUID.randomUUID();

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
    @DisplayName("GET /api/cart - should return cart when authenticated")
    void getCart_WhenAuthenticated_ReturnsCart() {
        when(cartService.getOrCreateCart(userUuid)).thenReturn(Mono.just(mockCart));
        when(cartService.toDtoWithProducts(mockCart)).thenReturn(Mono.just(mockCartDto));

        webTestClient.mutateWith(sessionUser(userUuid))
                .get().uri("/api/cart")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.uuid").isEqualTo(cartUuid.toString())
                .jsonPath("$.items[0].uuid").isEqualTo(itemUuid.toString());
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
    @DisplayName("POST /api/cart/items - should add item when authenticated")
    void addItemToCart_WhenAuthenticated_ReturnsUpdatedCart() {
        CartItemDto requestDto = CartItemDto.builder()
                .productUuid(productUuid)
                .quantity(1)
                .build();

        when(cartService.addItem(eq(userUuid), any(CartItemDto.class))).thenReturn(Mono.just(mockCart));
        when(cartService.toDtoWithProducts(mockCart)).thenReturn(Mono.just(mockCartDto));

        webTestClient.mutateWith(sessionUser(userUuid))
                .post().uri("/api/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.items[0].productUuid").isEqualTo(productUuid.toString());
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
    @DisplayName("PUT /api/cart/items/{itemUuid} - should return updated cart when authenticated")
    void updateItem_WhenAuthenticated_ReturnsUpdatedCart() {
        UpdateItemQuantityDto quantityDto = new UpdateItemQuantityDto();
        quantityDto.setQuantity(2);

        when(cartService.updateItem(eq(userUuid), eq(itemUuid), any(UpdateItemQuantityDto.class)))
                .thenReturn(Mono.just(mockCart));
        when(cartService.toDtoWithProducts(mockCart)).thenReturn(Mono.just(mockCartDto));

        webTestClient.mutateWith(sessionUser(userUuid))
                .put().uri("/api/cart/items/{itemUuid}", itemUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(quantityDto)
                .exchange()
                .expectStatus().isOk();
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

    @Test
    @DisplayName("DELETE /api/cart - should clear cart when authenticated")
    void cleanCart_WhenAuthenticated_ReturnsEmptyCart() {
        Cart emptyMockCart = Cart.builder().cartUuid(cartUuid).userUuid(userUuid).items(List.of()).build();
        CartDto emptyCartDto = CartDto.builder().uuid(cartUuid).userUuid(userUuid).items(List.of()).build();

        when(cartService.cleanCart(userUuid)).thenReturn(Mono.just(emptyMockCart));
        when(cartService.toDtoWithProducts(emptyMockCart)).thenReturn(Mono.just(emptyCartDto));

        webTestClient.mutateWith(sessionUser(userUuid))
                .delete().uri("/api/cart")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.items").isEmpty();
    }
}