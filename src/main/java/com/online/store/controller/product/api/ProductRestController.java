package com.online.store.controller.product.api;

import com.online.store.dto.product.ProductDto;
import com.online.store.mapper.product.ProductMapper;
import com.online.store.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public Page<ProductDto> getAll(@RequestParam(required = false) String query, Pageable pageable) {
        return productService.getAll(query, pageable)
                .map(productMapper::toDto);
    }

    @GetMapping("/{uuid}")
    public ProductDto getProduct(@PathVariable UUID uuid) {
        return productMapper.toDto(productService.getById(uuid));
    }
}
