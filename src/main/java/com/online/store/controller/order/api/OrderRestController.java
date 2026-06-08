package com.online.store.controller.order.api;

import com.online.store.dto.order.OrderDto;
import com.online.store.mapper.order.OrderMapper;
import com.online.store.model.order.Order;
import com.online.store.service.order.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderRestController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(HttpSession session) {
        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Order order = orderService.createOrder(userUuid);
        OrderDto orderDto = orderMapper.toDto(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }

    @GetMapping("/{orderUuid}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable UUID orderUuid, HttpSession session) {
        UUID userUuid = (UUID) session.getAttribute("user_id");

        if (userUuid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Order order = orderService.getOrder(orderUuid, userUuid);
        OrderDto orderDto = orderMapper.toDto(order);

        return ResponseEntity.ok(orderDto);
    }
}
