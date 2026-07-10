package com.online.store.model.order;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("orders")
public class Order implements Persistable<UUID> {

    @Id
    @Column("order_uuid")
    private UUID orderUuid;

    @Column("user_uuid")
    private UUID userUuid;

    @Column("status")
    private OrderStatus status;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Transient
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Nullable
    @Override
    public UUID getId() {
        return this.orderUuid;
    }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
