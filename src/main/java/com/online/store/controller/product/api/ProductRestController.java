package com.online.store.controller.product.api;

import com.online.store.dto.product.ProductDto;
import com.online.store.mapper.product.ProductMapper;
import com.online.store.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public Mono<Page<ProductDto>> getAll(@RequestParam(required = false) String query, Pageable pageable) {
        return Mono.zip(
                productService.getAll(query, pageable).collectList(),
                productService.countAll(query)
        )
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2())
                        .map(productMapper::toDto));
    }

    @GetMapping("/{uuid}")
    public Mono<ProductDto> getProduct(@PathVariable UUID uuid) {
        return productService.getById(uuid).map(productMapper::toDto);
    }
}
