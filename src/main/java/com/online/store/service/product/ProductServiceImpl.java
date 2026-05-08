package com.online.store.service.product;

import com.online.store.exception.ProductNotFoundException;
import com.online.store.model.Product;
import com.online.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Page<Product> getAll(String query, Pageable pageable) {
        if (query != null && !query.isBlank()) {
            return productRepository.findByNameContainingIgnoreCase(query, pageable);
        }
        return productRepository.findAll(pageable);
    }

    @Override
    public Product getById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
