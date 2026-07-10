package com.online.store.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private UUID uuid;
    private UUID productUuid;
    private String productName;
    private String imageUrl;
    private BigDecimal totalPrice;
    private Integer quantity;
}
