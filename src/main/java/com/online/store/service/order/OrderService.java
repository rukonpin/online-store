package com.online.store.service.order;

import com.online.store.model.order.Order;
import com.online.store.model.order.OrderItem;
import com.online.store.model.order.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order createOrder(UUID userUuid);
    Order getOrder(UUID orderUuid, UUID userUuid);
    List<Order> getAllOrders(UUID userUuid);
}
