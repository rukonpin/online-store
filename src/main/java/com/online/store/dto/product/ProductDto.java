package com.online.store.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductDto {
    private UUID uuid;
    private String name;
    private String imageUrl;
    private BigDecimal price;
}
