package com.online.store.model.order;

import com.online.store.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_uuid", updatable = false, nullable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "user_uuid", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public BigDecimal getTotalPrice() {
        if (items == null) {
            return BigDecimal.ZERO;
        }

        return items.stream()
                .map(OrderItem::getPriceAtPurchase)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
