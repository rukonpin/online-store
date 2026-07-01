package com.online.store.model.cart;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table("cart_items")
public class CartItem implements Persistable<UUID> {

    @Id
    @Column("item_uuid")
    private UUID itemUuid;

    @Column("cart_uuid")
    private UUID cartUuid;

    @Column("product_uuid")
    private UUID productUuid;

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
