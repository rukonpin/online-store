package com.online.store.model.order;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("order_items")
public class OrderItem implements Persistable<UUID> {

    @Id
    @Column("order_item_uuid")
    private UUID itemUuid;

    @Column("order_uuid")
    private UUID orderUuid;

    @Column("product_uuid")
    private UUID productUuid;

    @Column("price_at_purchase")
    private BigDecimal priceAtPurchase;

    @Column("quantity")
    private Integer quantity;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @Nullable
    @Override
    public UUID getId() {
        return this.itemUuid;
    }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
