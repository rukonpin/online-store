package com.online.store.model.cart;

import com.online.store.model.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue
    @Column(name = "item_uuid", updatable = false, nullable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "cart_uuid", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_uuid", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    public BigDecimal getTotalPrice() {
        if (product == null || product.getPrice() == null || quantity == null) {
            return BigDecimal.ZERO;
        }

        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
