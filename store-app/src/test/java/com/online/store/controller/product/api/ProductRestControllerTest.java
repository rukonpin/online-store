package com.online.store.controller.product.api;

import com.online.store.dto.product.ProductDto;
import com.online.store.exception.product.ProductNotFoundException;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;


@WebFluxTest(ProductRestController.class)
@Import(ProductRestControllerTest.TestWebFluxConfig.class)
class ProductRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    private Product testProduct;
    private ProductDto testProductDto;
    private UUID testUuid;

    @TestConfiguration
    static class TestWebFluxConfig implements WebFluxConfigurer {
        @Override
        public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
            configurer.addCustomResolver(new ReactivePageableHandlerMethodArgumentResolver());
        }
    }

    @BeforeEach
    void setUp() {
        testUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        testProduct = Product.builder()
                .productUuid(testUuid)
                .name("Test Product")
                .description("Test Description")
                .imageUrl("http://test.com/image.jpeg")
                .price(BigDecimal.TEN)
                .build();

        testProductDto = ProductDto.builder()
                .uuid(testUuid)
                .name("Test Product")
                .imageUrl("http://test.com/image.jpeg")
                .price(BigDecimal.TEN)
                .build();
    }

    @Test
    @DisplayName("GET /api/products - should return all products")
    void getAll_WithoutQuery_ReturnsAllProducts() {
        when(productService.getAll(eq(null), any(Pageable.class)))
                .thenReturn(Flux.just(testProduct));
        when(productService.countAll(null))
                .thenReturn(Mono.just(1L));
        when(productMapper.toDto(testProduct))
                .thenReturn(testProductDto);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products")
                        .queryParam("sort", "price,asc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content[0].uuid").isEqualTo(testUuid.toString())
                .jsonPath("$.content[0].name").isEqualTo("Test Product")
                .jsonPath("$.content[0].price").isEqualTo(10);
    }

    @Test
    @DisplayName("GET /api/products?query=Test - should search products")
    void getAll_WithQuery_SearchProducts() {
        String searchQuery = "Test";

        when(productService.getAll(eq(searchQuery), any(Pageable.class)))
                .thenReturn(Flux.just(testProduct));
        when(productService.countAll(searchQuery))
                .thenReturn(Mono.just(1L));
        when(productMapper.toDto(testProduct))
                .thenReturn(testProductDto);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products")
                        .queryParam("query", searchQuery)
                        .queryParam("sort", "price,asc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].name").isEqualTo("Test Product");
    }

    @Test
    @DisplayName("GET /api/products?page=0&size=10 - should return products")
    void getAll_WithPageable_ReturnsProducts() {
        when(productService.getAll(eq(null), any(Pageable.class)))
                .thenReturn(Flux.just(testProduct));
        when(productService.countAll(null))
                .thenReturn(Mono.just(1L));
        when(productMapper.toDto(testProduct))
                .thenReturn(testProductDto);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .queryParam("sort", "price,asc")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.pageable.pageSize").isEqualTo(10)
                .jsonPath("$.pageable.pageNumber").isEqualTo(0)
                .jsonPath("$.totalElements").isEqualTo(1)
                .jsonPath("$.totalPages").isEqualTo(1);
    }

    @Test
    @DisplayName("GET /api/products/{uuid} - should return product with current uuid")
    void getProduct_WithUuid_ReturnsProduct() {
        when(productService.getById(testUuid))
                .thenReturn(Mono.just(testProduct));
        when(productMapper.toDto(testProduct))
                .thenReturn(testProductDto);

        webTestClient.get().uri("/api/products/{uuid}", testUuid)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.uuid").isEqualTo(testUuid.toString())
                .jsonPath("$.name").isEqualTo("Test Product");
    }

    @Test
    @DisplayName("GET /api/products/{uuid} - should throw ProductNotFoundException when product not found")
    void getProduct_WithNonExistentProduct_ThrowsProductNotFoundException() {
        UUID uuid = UUID.randomUUID();

        when(productService.getById(uuid))
                .thenReturn(Mono.error(new ProductNotFoundException(uuid)));

        webTestClient.get().uri("/api/products/{uuid}", uuid)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Product with this " + uuid + " not found");
    }
}