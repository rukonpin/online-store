package com.online.store.controller.product.web;

import com.online.store.dto.product.ProductDto;
import com.online.store.mapper.product.ProductMapper;
import com.online.store.model.product.Product;
import com.online.store.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductViewController.class)
class ProductViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    @Test
    @DisplayName("GET /products - should return page catalog with products")
    void catalog_WithProducts_ReturnsIndexView() throws Exception {

        String query = "iphone";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "price"));

        Product product = Product.builder().name("iPhone 17").build();
        ProductDto productDto = ProductDto.builder().name("iPhone 17").build();

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productService.getAll(eq(query), eq(pageable))).thenReturn(productPage);
        when(productMapper.toDto(product)).thenReturn(productDto);

        mockMvc.perform(get("/products")
                    .param("query", query))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("query", query))
                .andExpect(model().attribute("products", hasProperty("content", hasSize(1))));
    }
}