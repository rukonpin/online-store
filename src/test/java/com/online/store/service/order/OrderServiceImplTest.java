package com.online.store.service.order;

import com.online.store.exception.cart.CartIsEmptyException;
import com.online.store.exception.order.OrderNotFoundException;
import com.online.store.model.cart.Cart;
import com.online.store.model.cart.CartItem;
import com.online.store.model.order.Order;
import com.online.store.model.order.OrderItem;
import com.online.store.model.order.OrderStatus;
import com.online.store.model.product.Product;
import com.online.store.model.user.User;
import com.online.store.repository.order.OrderRepository;
import com.online.store.service.cart.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Should return order when user purchased on order")
    void createOrder_WhenUserPurchasedOrder_ReturnOrder() {
        UUID userUuid = UUID.randomUUID();
        UUID orderUuid = UUID.randomUUID();
        UUID productUuid = UUID.randomUUID();

        User userMock = User.builder().uuid(userUuid).build();

        Product productMock = Product.builder()
                .uuid(productUuid)
                .price(BigDecimal.valueOf(100))
                .build();

        CartItem cartItem = CartItem.builder()
                .product(productMock)
                .quantity(1)
                .build();

        List<CartItem> existingCartItems = new ArrayList<>();
        existingCartItems.add(cartItem);

        Cart mockCart = Cart.builder()
                .uuid(UUID.randomUUID())
                .user(userMock)
                .items(existingCartItems)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .uuid(UUID.randomUUID())
                .product(productMock)
                .priceAtPurchase(BigDecimal.valueOf(100))
                .quantity(1)
                .build();

        List<OrderItem> existingOrderItems = new ArrayList<>();
        existingOrderItems.add(orderItem);

        Order mockOrder = Order.builder()
                .uuid(orderUuid)
                .user(userMock)
                .items(existingOrderItems)
                .status(OrderStatus.PENDING)
                .build();

        when(cartService.getOrCreateCart(userUuid))
                .thenReturn(mockCart);
        when(orderRepository.save(any(Order.class)))
                .thenReturn(mockOrder);

        Order result = orderService.createOrder(userUuid);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order saveOrder = orderCaptor.getValue();

        assertNotNull(saveOrder);
        assertEquals(OrderStatus.PENDING, saveOrder.getStatus());
        assertEquals(userUuid, saveOrder.getUser().getUuid());
        assertEquals(1, saveOrder.getItems().size());

        verify(cartService).cleanCart(userUuid);

        assertNotNull(result);
        assertEquals(orderUuid, result.getUuid());
    }

    @Test
    @DisplayName("Should throw CartIsEmptyException when empty cart")
    void createOrder_WhenEmptyCart_ThrowCartIsEmptyException() {
        UUID userUuid = UUID.randomUUID();

        User userMock = User.builder().uuid(userUuid).build();

        Cart mockCart = Cart.builder()
                .uuid(UUID.randomUUID())
                .user(userMock)
                .items(new ArrayList<>())
                .build();

        when(cartService.getOrCreateCart(userUuid))
                .thenReturn(mockCart);

        assertThrows(CartIsEmptyException.class,
                () -> orderService.createOrder(userUuid));
    }

    @Test
    @DisplayName("Should return order uuid when purchased exists")
    void getOrder_WhenPurchasedExists_ReturnOrder() {
        UUID userUuid = UUID.randomUUID();
        UUID orderUuid = UUID.randomUUID();
        UUID productUuid = UUID.randomUUID();

        User userMock = User.builder().uuid(userUuid).build();

        Product productMock = Product.builder()
                .uuid(productUuid)
                .price(BigDecimal.valueOf(100))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .uuid(UUID.randomUUID())
                .product(productMock)
                .priceAtPurchase(BigDecimal.valueOf(100))
                .quantity(1)
                .build();

        List<OrderItem> existingOrderItems = new ArrayList<>();
        existingOrderItems.add(orderItem);

        Order mockOrder = Order.builder()
                .uuid(orderUuid)
                .user(userMock)
                .items(existingOrderItems)
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(orderUuid))
                .thenReturn(Optional.of(mockOrder));

        Order result = orderService.getOrder(orderUuid, userUuid);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(userUuid, result.getUser().getUuid());
        assertEquals(orderUuid, result.getUuid());
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order not found")
    void getOrder_WhenOrderNotFound_ThrowOrderNotFoundException() {

        when(orderRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrder(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void getOrder_WhenUserNotFound_ThrowUserNotFoundException() {
        UUID userUuid = UUID.randomUUID();
        UUID orderUuid = UUID.randomUUID();

        User userMock = User.builder().uuid(UUID.randomUUID()).build();

        Product productMock = Product.builder()
                .uuid(UUID.randomUUID())
                .price(BigDecimal.valueOf(100))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .uuid(UUID.randomUUID())
                .product(productMock)
                .build();

        List<OrderItem> existingOrderItems = new ArrayList<>();
        existingOrderItems.add(orderItem);

        Order mockOrder = Order.builder()
                .uuid(orderUuid)
                .user(userMock)
                .items(existingOrderItems)
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(orderUuid))
                .thenReturn(Optional.of(mockOrder));

        assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrder(orderUuid, userUuid));
    }
}