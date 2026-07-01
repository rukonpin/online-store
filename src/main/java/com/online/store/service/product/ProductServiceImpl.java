package com.online.store.service.product;

import com.online.store.exception.product.ProductNotFoundException;
import com.online.store.model.product.Product;
import com.online.store.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Flux<Product> getAll(String query, Pageable pageable) {
        if (query != null && !query.isBlank()) {
            return productRepository.findByNameContainingIgnoreCase(query, pageable);
        }

        boolean isDesc = pageable.getSort()
                                 .getOrderFor("price") != null
                         && pageable.getSort()
                                 .getOrderFor("price")
                                 .isDescending();

        int limit = pageable.getPageSize();
        long offset = pageable.getOffset();

        return isDesc
                ? productRepository.findAllOrderByPriceDesc(limit, offset)
                : productRepository.findAllOrderByPriceAsc(limit, offset);
    }

    @Override
    public Mono<Long> countAll(String query) {
        if (query != null && !query.isBlank()) {
            return productRepository.countByNameContainingIgnoreCase(query);
        }
        return productRepository.count();
    }

    @Override
    public Mono<Product> getById(UUID id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)));
    }
}
