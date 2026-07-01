package com.online.store.model.cart;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("carts")
public class Cart implements Persistable<UUID> {

    @Id
    @Column("cart_uuid")
    private UUID cartUuid;

    @Column("user_uuid")
    private UUID userUuid;

    @Transient
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Nullable
    @Override
    public UUID getId() {
        return this.cartUuid;
    }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
