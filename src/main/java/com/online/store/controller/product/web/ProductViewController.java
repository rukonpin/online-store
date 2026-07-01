package com.online.store.controller.product.web;

import com.online.store.mapper.product.ProductMapper;
import com.online.store.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductViewController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public Mono<String> catalog(
            @RequestParam(required = false) String query,
            @PageableDefault(sort = "price", direction = Sort.Direction.ASC) Pageable pageable,
            Model model) {

        return Mono.zip(
                productService.getAll(query, pageable).collectList(),
                productService.countAll(query)
        )
                    .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                    .map(page -> page.map(productMapper::toDto))
                    .doOnNext(page -> {
                        model.addAttribute("products", page);
                        model.addAttribute("query", query);
                        model.addAttribute("sort", pageable.getSort().isSorted()
                                ? pageable.getSort().iterator().next().getProperty()
                                  + "," + pageable.getSort().iterator().next().getDirection().name().toLowerCase()
                                : "price,asc");
                    })
                    .map(page -> "index");
    }

    @GetMapping("/{productUuid}")
    public Mono<String> productPage(@PathVariable UUID productUuid, Model model) {
        return productService.getById(productUuid)
                .map(productMapper::toDto)
                .doOnNext(product -> model.addAttribute("product", product))
                .map(product -> "product");
    }

}
