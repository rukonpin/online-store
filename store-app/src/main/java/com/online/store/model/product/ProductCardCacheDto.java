package com.online.store.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCardCacheDto implements Serializable {
    private UUID uuid;
    private String name;
    private String imageUrl;
    private BigDecimal price;
    private String description;
}
