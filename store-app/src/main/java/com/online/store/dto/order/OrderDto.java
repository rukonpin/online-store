package com.online.store.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.online.store.model.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID uuid;
    private UUID userUuid;
    private OrderStatus status;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<OrderItemDto> items = new ArrayList<>();

    @JsonIgnore
    public BigDecimal getTotalPrice() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> item.getPriceAtPurchase() != null ? item.getPriceAtPurchase() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
