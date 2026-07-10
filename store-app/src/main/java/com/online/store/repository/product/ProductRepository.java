package com.online.store.repository.product;

import com.online.store.model.product.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductRepository extends R2dbcRepository<Product, UUID> {
    Flux<Product> findByNameContainingIgnoreCase(String query, Pageable pageable);
    Mono<Long> countByNameContainingIgnoreCase(String query);

    @Query("SELECT * FROM products ORDER BY price ASC LIMIT :limit OFFSET :offset")
    Flux<Product> findAllOrderByPriceAsc(int limit, long offset);

    @Query("SELECT * FROM products ORDER BY price DESC LIMIT :limit OFFSET :offset")
    Flux<Product> findAllOrderByPriceDesc(int limit, long offset);
}
