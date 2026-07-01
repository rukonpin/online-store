package com.online.store.service.order;

import com.online.store.dto.order.OrderDto;
import com.online.store.exception.cart.CartIsEmptyException;
import com.online.store.exception.order.OrderNotFoundException;
import com.online.store.mapper.order.OrderMapper;
import com.online.store.model.cart.Cart;
import com.online.store.model.order.Order;
import com.online.store.model.order.OrderItem;
import com.online.store.model.order.OrderStatus;
import com.online.store.repository.order.OrderItemRepository;
import com.online.store.repository.order.OrderRepository;
import com.online.store.service.cart.CartService;
import com.online.store.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public Mono<Order> createOrder(UUID userUuid) {
        return cartService.getOrCreateCart(userUuid)
                .flatMap(cart -> {
                    if (cart.getItems().isEmpty()) {
                        return Mono.error(new CartIsEmptyException(userUuid));
                    }

                    Order order = Order.builder()
                            .orderUuid(UUID.randomUUID())
                            .userUuid(cart.getUserUuid())
                            .status(OrderStatus.PENDING)
                            .build();

                    return orderRepository.save(order)
                            .flatMap(savedOrder ->
                                    // 3. Строим список позиций заказа на основе корзины
                                    buildOrderItems(savedOrder, cart)
                                            .flatMap(orderItems ->
                                                    // 4. ФИЗИЧЕСКИ СОХРАНЯЕМ все OrderItem в БД
                                                    orderItemRepository.saveAll(orderItems)
                                                            .collectList()
                                                            .doOnNext(savedOrder::setItems)
                                                            .thenReturn(savedOrder)
                                            )
                            )
                            // 5. Очищаем корзину пользователя после успешного оформления заказа
                            .flatMap(savedOrder -> cartService.cleanCart(userUuid)
                                    .thenReturn(savedOrder));
                });
    }

    private Mono<List<OrderItem>> buildOrderItems(Order order, Cart cart) {
        return Flux.fromIterable(cart.getItems())
                .flatMap(cartItem -> productService.getById(cartItem.getProductUuid())
                        .map(product -> OrderItem.builder()
                                .itemUuid(UUID.randomUUID())
                                .orderUuid(order.getOrderUuid())
                                .productUuid(cartItem.getProductUuid())
                                .priceAtPurchase(product.getPrice())
                                .quantity(cartItem.getQuantity())
                                .build()))
                .collectList();
    }

    private Mono<Order> loadOrderWithItems(Order order) {
        return orderItemRepository.findAllByOrderUuid(order.getOrderUuid())
                .collectList()
                .map(items -> {
                    order.setItems(items);
                    return order;
                });
    }

    @Override
    @Transactional
    public Mono<Order> getOrder(UUID orderUuid, UUID userUuid) {
        return orderRepository.findById(orderUuid)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(orderUuid)))
                .flatMap(order -> {
                    if (!order.getUserUuid().equals(userUuid)) {
                        return Mono.error(new OrderNotFoundException(orderUuid));
                    }

                    return loadOrderWithItems(order);
                });

    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Order> getAllOrders(UUID userUuid) {
        return orderRepository.findAllByUserUuidOrderByCreatedAtDesc(userUuid)
                .flatMapSequential(this::loadOrderWithItems);
    }

    @Override
    public Mono<OrderDto> toDtoWithProducts(Order order) {
        OrderDto orderDto = orderMapper.toDto(order);

        return Flux.fromIterable(order.getItems())
                .flatMapSequential(orderItem -> productService.getById(orderItem.getProductUuid())
                        .map(product -> orderMapper.toDto(orderItem, product)))
                .collectList()
                .doOnNext(orderDto::setItems)
                .thenReturn(orderDto);
    }
}
