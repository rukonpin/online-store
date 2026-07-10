package com.online.store.model.product;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table("products")
public class Product implements Persistable<UUID> {

    @Id
    @Column("product_uuid")
    private UUID productUuid;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("image_url")
    private String imageUrl;

    @Column("price")
    private BigDecimal price;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Nullable
    @Override
    public UUID getId() {
        return this.productUuid;
    }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
