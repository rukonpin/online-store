package com.online.store.service.product;

import com.online.store.model.product.Product;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductService {
    Flux<Product> getAll(String query, Pageable pageable);
    Mono<Long> countAll(String query);
    Mono<Product> getById(UUID id);
}
