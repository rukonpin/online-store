package com.online.store.dto.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OrderItemDto {
    private UUID uuid;
    private UUID productUuid;
    private String productName;
    private String imageUrl;
    private BigDecimal priceAtPurchase;
    private Integer quantity;

}
