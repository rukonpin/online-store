package com.online.store.service.product;

import com.online.store.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    Page<Product> getAll(String query, Pageable pageable);
    Product getById(UUID id);
}
