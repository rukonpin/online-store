package com.online.store.service.order;

import com.online.store.exception.cart.CartIsEmptyException;
import com.online.store.exception.order.OrderNotFoundException;
import com.online.store.model.cart.Cart;
import com.online.store.model.order.Order;
import com.online.store.model.order.OrderItem;
import com.online.store.model.order.OrderStatus;
import com.online.store.repository.order.OrderRepository;
import com.online.store.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public Order createOrder(UUID userUuid) {
        Cart cart = cartService.getOrCreateCart(userUuid);

        if (cart.getItems().isEmpty()) {
            throw new CartIsEmptyException(userUuid);
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .product(cartItem.getProduct())
                        .priceAtPurchase(cartItem.getProduct().getPrice())
                        .quantity(cartItem.getQuantity())
                        .build()
                )
                .collect(Collectors.toList());

        order.setItems(orderItems);

        cartService.cleanCart(userUuid);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order getOrder(UUID orderUuid, UUID userUuid) {

        Order order = orderRepository.findById(orderUuid)
                .orElseThrow(() -> new OrderNotFoundException(orderUuid));

        if (!order.getUser().getUuid().equals(userUuid)) {
            throw new OrderNotFoundException(orderUuid);
        }

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders(UUID userUuid) {
        return orderRepository.findAllByUserUuidOrderByCreatedAtDesc(userUuid);
    }
}
