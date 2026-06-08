package com.online.store.repository.order;

import com.online.store.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByUserUuidOrderByCreatedAtDesc(UUID userUuid);
}
