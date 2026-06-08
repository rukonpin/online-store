package com.online.store.dto.order;

import com.online.store.model.order.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderDto {
    private UUID uuid;
    private UUID userUuid;
    private OrderStatus status;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<OrderItemDto> items = new ArrayList<>();

    public BigDecimal getTotalPrice() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> item.getPriceAtPurchase() != null ? item.getPriceAtPurchase() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
