package com.online.store.controller.product.web;

import com.online.store.dto.product.ProductDto;
import com.online.store.mapper.product.ProductMapper;
import com.online.store.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductViewController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public String catalog(
            @RequestParam(required = false) String query,
            @PageableDefault(sort = "price", direction = Sort.Direction.ASC) Pageable pageable,
            Model model) {

        Page<ProductDto> products = productService.getAll(query, pageable)
                .map(productMapper::toDto);

        model.addAttribute("products", products);
        model.addAttribute("query", query);

        return "index";
    }

    @GetMapping("/{productUuid}")
    public String productPage(@PathVariable UUID productUuid, Model model) {
        ProductDto product = productMapper.toDto(productService.getById(productUuid));
        model.addAttribute("product", product);
        return "product";
    }

}
