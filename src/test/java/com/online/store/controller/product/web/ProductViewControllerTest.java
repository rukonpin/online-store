package com.online.store.controller.product.web;

import com.online.store.dto.product.ProductDto;
import com.online.store.mapper.product.ProductMapper;
import com.online.store.model.product.Product;
import com.online.store.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(ProductViewController.class)
@Import(ProductViewControllerTest.TestWebFluxConfig.class)
class ProductViewControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    private Product testProduct;
    private ProductDto testProductDto;

    @TestConfiguration
    static class TestWebFluxConfig implements WebFluxConfigurer {
        @Override
        public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
            configurer.addCustomResolver(new ReactivePageableHandlerMethodArgumentResolver());
        }
    }

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .productUuid(UUID.randomUUID())
                .name("iPhone 17")
                .price(BigDecimal.valueOf(159990))
                .build();

        testProductDto = ProductDto.builder()
                .uuid(testProduct.getProductUuid())
                .name("iPhone 17")
                .price(BigDecimal.valueOf(159990))
                .build();
    }

    @Test
    @DisplayName("GET /products - should return 200 and index view")
    void catalog_WithProducts_ReturnsIndexView() {
        when(productService.getAll(eq(null), any(Pageable.class)))
                .thenReturn(Flux.just(testProduct));
        when(productService.countAll(null))
                .thenReturn(Mono.just(1L));
        when(productMapper.toDto(testProduct))
                .thenReturn(testProductDto);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products")
                        .queryParam("sort", "price,asc")
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("GET /products?query=iphone - should return 200 with filtered products")
    void catalog_WithQuery_ReturnsFilteredProducts() {
        String query = "iphone";

        when(productService.getAll(eq(query), any(Pageable.class)))
                .thenReturn(Flux.just(testProduct));
        when(productService.countAll(query))
                .thenReturn(Mono.just(1L));
        when(productMapper.toDto(testProduct))
                .thenReturn(testProductDto);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products")
                        .queryParam("query", query)
                        .queryParam("sort", "price,asc")
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("GET /products - should return 200 when no products found")
    void catalog_WithNoProducts_ReturnsEmptyPage() {
        when(productService.getAll(eq(null), any(Pageable.class)))
                .thenReturn(Flux.empty());
        when(productService.countAll(null))
                .thenReturn(Mono.just(0L));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products")
                        .queryParam("sort", "price,asc")
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("GET /products/{productUuid} - should return 200 and product view")
    void productPage_WithValidUuid_ReturnsProductView() {
        UUID productUuid = testProduct.getProductUuid();

        when(productService.getById(productUuid))
                .thenReturn(Mono.just(testProduct));
        when(productMapper.toDto(testProduct))
                .thenReturn(testProductDto);

        webTestClient.get().uri("/products/{productUuid}", productUuid)
                .exchange()
                .expectStatus().isOk();
    }
}