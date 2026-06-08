package com.online.store.model.order;

import com.online.store.model.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item", unique = true, nullable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "order_uuid", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_uuid", nullable = false)
    private Product product;

    @Column(nullable = false)
    private BigDecimal priceAtPurchase;

    @Column(nullable = false)
    private Integer quantity;

    public BigDecimal getPriceAtPurchase() {
        if (priceAtPurchase == null || quantity == null) {
            return BigDecimal.ZERO;
        }

        return priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
    }
}
