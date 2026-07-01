package com.online.store.service.order;

import com.online.store.exception.cart.CartIsEmptyException;
import com.online.store.exception.order.OrderNotFoundException;
import com.online.store.model.cart.Cart;
import com.online.store.model.cart.CartItem;
import com.online.store.model.order.Order;
import com.online.store.model.order.OrderItem;
import com.online.store.model.order.OrderStatus;
import com.online.store.model.product.Product;
import com.online.store.repository.order.OrderItemRepository;
import com.online.store.repository.order.OrderRepository;
import com.online.store.service.cart.CartService;
import com.online.store.service.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Should return order when user purchased on order")
    void createOrder_WhenUserPurchasedOrder_ReturnOrder() {
        UUID userUuid = UUID.randomUUID();
        UUID orderUuid = UUID.randomUUID();
        UUID productUuid = UUID.randomUUID();

        Product productMock = Product.builder()
                .productUuid(productUuid)
                .price(BigDecimal.valueOf(100))
                .build();

        CartItem cartItem = CartItem.builder()
                .itemUuid(UUID.randomUUID())
                .productUuid(productUuid)
                .quantity(1)
                .build();

        List<CartItem> existingCartItems = new ArrayList<>();
        existingCartItems.add(cartItem);

        Cart mockCart = Cart.builder()
                .cartUuid(UUID.randomUUID())
                .userUuid(userUuid)
                .items(existingCartItems)
                .build();

        when(cartService.getOrCreateCart(userUuid))
                .thenReturn(Mono.just(mockCart));
        when(productService.getById(productUuid))
                .thenReturn(Mono.just(productMock));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order toSave = invocation.getArgument(0);
                    Order saved = Order.builder()
                            .orderUuid(orderUuid)
                            .userUuid(toSave.getUserUuid())
                            .status(toSave.getStatus())
                            .items(new ArrayList<>())
                            .build();
                    return Mono.just(saved);
                });

        when(orderItemRepository.saveAll(any(Iterable.class)))
                .thenAnswer(invocation -> {
                    Iterable<OrderItem> items = invocation.getArgument(0);
                    return Flux.fromIterable(items);
                });

        when(cartService.cleanCart(userUuid))
                .thenReturn(Mono.just(mockCart));

        StepVerifier.create(orderService.createOrder(userUuid))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(orderUuid, result.getOrderUuid());
                    assertEquals(OrderStatus.PENDING, result.getStatus());
                    assertEquals(userUuid, result.getUserUuid());
                    assertEquals(1, result.getItems().size());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw CartIsEmptyException when empty cart")
    void createOrder_WhenEmptyCart_ThrowCartIsEmptyException() {
        UUID userUuid = UUID.randomUUID();

        Cart mockCart = Cart.builder()
                .cartUuid(UUID.randomUUID())
                .userUuid(userUuid)
                .items(new ArrayList<>())
                .build();

        when(cartService.getOrCreateCart(userUuid))
                .thenReturn(Mono.just(mockCart));

        StepVerifier.create(orderService.createOrder(userUuid))
                .expectError(CartIsEmptyException.class)
                .verify();

        verify(orderRepository, never()).save(any(Order.class));
        verify(cartService, never()).cleanCart(any());

    }

    @Test
    @DisplayName("Should return order uuid when purchased exists")
    void getOrder_WhenPurchasedExists_ReturnOrder() {
        UUID userUuid = UUID.randomUUID();
        UUID orderUuid = UUID.randomUUID();

        Order mockOrder = Order.builder()
                .orderUuid(orderUuid)
                .userUuid(userUuid)
                .status(OrderStatus.PENDING)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .itemUuid(UUID.randomUUID())
                .orderUuid(orderUuid)
                .productUuid(UUID.randomUUID())
                .priceAtPurchase(BigDecimal.TEN)
                .quantity(1)
                .build();

        when(orderRepository.findById(orderUuid))
                .thenReturn(Mono.just(mockOrder));
        when(orderItemRepository.findAllByOrderUuid(orderUuid))
                .thenReturn(Flux.just(orderItem));

        StepVerifier.create(orderService.getOrder(orderUuid, userUuid))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(OrderStatus.PENDING, result.getStatus());
                    assertEquals(userUuid, result.getUserUuid());
                    assertEquals(orderUuid, result.getOrderUuid());
                    assertEquals(1, result.getItems().size());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order not found")
    void getOrder_WhenOrderNotFound_ThrowOrderNotFoundException() {
        UUID orderUuid = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();

        when(orderRepository.findById(orderUuid))
                .thenReturn(Mono.empty());

        StepVerifier.create(orderService.getOrder(orderUuid, userUuid))
                .expectError(OrderNotFoundException.class)
                .verify();

    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void getOrder_WhenUserNotFound_ThrowUserNotFoundException() {
        UUID userUuid = UUID.randomUUID();
        UUID orderUuid = UUID.randomUUID();

        Order mockOrder = Order.builder()
                .orderUuid(orderUuid)
                .userUuid(UUID.randomUUID())
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(orderUuid))
                .thenReturn(Mono.just(mockOrder));

        StepVerifier.create(orderService.getOrder(orderUuid, userUuid))
                .expectError(OrderNotFoundException.class)
                .verify();

    }
}