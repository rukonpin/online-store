package com.online.store.controller.order.api;

import com.online.store.dto.order.OrderDto;
import com.online.store.dto.order.OrderItemDto;
import com.online.store.exception.order.OrderNotFoundException;
import com.online.store.mapper.order.OrderMapper;
import com.online.store.model.order.Order;
import com.online.store.model.order.OrderItem;
import com.online.store.model.order.OrderStatus;
import com.online.store.model.product.Product;
import com.online.store.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebFluxTest(OrderRestController.class)
class OrderRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    private UUID userUuid;
    private UUID orderUuid;
    private UUID productUuid;
    private Order mockOrder;
    private OrderDto mockOrderDto;

    @BeforeEach
    void setUp() {
        userUuid = UUID.randomUUID();
        orderUuid = UUID.randomUUID();
        productUuid = UUID.randomUUID();

        Product productMock = Product.builder()
                .productUuid(productUuid)
                .price(BigDecimal.valueOf(100))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .itemUuid(UUID.randomUUID())
                .productUuid(productUuid)
                .priceAtPurchase(BigDecimal.valueOf(100))
                .quantity(1)
                .build();

        List<OrderItem> existingOrderItems = new ArrayList<>();
        existingOrderItems.add(orderItem);

        mockOrder = Order.builder()
                .orderUuid(orderUuid)
                .userUuid(userUuid)
                .items(existingOrderItems)
                .status(OrderStatus.PENDING)
                .build();

        OrderItemDto itemResponseDto = OrderItemDto.builder()
                .uuid(orderItem.getItemUuid())
                .productUuid(productUuid)
                .priceAtPurchase(BigDecimal.valueOf(100))
                .quantity(1)
                .build();

        mockOrderDto = OrderDto.builder()
                .uuid(orderUuid)
                .userUuid(userUuid)
                .items(List.of(itemResponseDto))
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("POST /api/orders - should return 201 and order when user authenticated")
    void createOrder_WhenUserPurchased_ReturnsOrder() {
        when(orderService.createOrder(userUuid)).thenReturn(Mono.just(mockOrder));
        when(orderService.toDtoWithProducts(mockOrder)).thenReturn(Mono.just(mockOrderDto));

        webTestClient.post().uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie("SESSION", "dummy")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("POST /api/orders - should return 401 when user not authenticated")
    void createOrder_WhenNotAuthenticated_ReturnsUnauthorized() {
        webTestClient.post().uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("GET /api/orders/{orderUuid} - should return 401 when user not authenticated")
    void getOrder_WhenNotAuthenticated_ReturnsUnauthorized() {
        webTestClient.get().uri("/api/orders/{orderUuid}", orderUuid)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("GET /api/orders/{orderUuid} - should return 404 when order not found")
    void getOrder_WhenOrderDoesNotExist_ReturnsNotFound() {
        when(orderService.getOrder(orderUuid, userUuid))
                .thenReturn(Mono.error(new OrderNotFoundException(orderUuid)));
        when(orderService.toDtoWithProducts(mockOrder)).thenReturn(Mono.just(mockOrderDto));

        webTestClient.get().uri("/api/orders/{orderUuid}", orderUuid)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}