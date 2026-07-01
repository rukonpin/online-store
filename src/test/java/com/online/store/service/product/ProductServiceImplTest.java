package com.online.store.service.product;

import com.online.store.exception.product.ProductNotFoundException;
import com.online.store.model.product.Product;
import com.online.store.repository.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product mockProduct;
    private UUID id;
    private Pageable pageableAsc;
    private Pageable pageableDesc;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        mockProduct = Product.builder()
                .productUuid(id)
                .name("Test Product")
                .price(BigDecimal.TEN)
                .build();

        pageableAsc = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("price")));
        pageableDesc = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("price")));
    }


    @Test
    @DisplayName("Should return product when found by UUID")
    void getById_WhenProductExists_ReturnsProduct() {
        when(productRepository.findById(id))
                .thenReturn(Mono.just(mockProduct));

        StepVerifier.create(productService.getById(id))
                .assertNext(result -> {
                    assertEquals(id, result.getProductUuid());
                    assertEquals("Test Product", result.getName());
                })
                .verifyComplete();

        verify(productRepository).findById(id);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found")
    void getById_WhenProductNotFound_ThrowsException() {
        when(productRepository.findById(id))
                .thenReturn(Mono.empty());

        StepVerifier.create(productService.getById(id))
                .expectError(ProductNotFoundException.class)
                .verify();

        verify(productRepository).findById(id);
    }

    @Test
    @DisplayName("Should return products ordered by price ASC when query null and order ASC")
    void getAll_WhenQueryIsNull_ReturnsProductsOrderedByPriceAsc() {
        when(productRepository.findAllOrderByPriceAsc(10, 0L))
                .thenReturn(Flux.just(mockProduct));

        StepVerifier.create(productService.getAll(null, pageableAsc))
                .expectNext(mockProduct)
                .verifyComplete();

        verify(productRepository).findAllOrderByPriceAsc(10, 0L);
        verify(productRepository, never()).findAllOrderByPriceDesc(anyInt(), anyLong());
        verify(productRepository, never()).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    @DisplayName("Should return products ordered by price DESC when query null and order DESC")
    void getAll_WhenQueryIsNullAndOrderIsDesc_ReturnsProductsOrderedByPriceDesc() {
        when(productRepository.findAllOrderByPriceDesc(10, 0L))
                .thenReturn(Flux.just(mockProduct));

        StepVerifier.create(productService.getAll(null, pageableDesc))
                .expectNext(mockProduct)
                .verifyComplete();

        verify(productRepository).findAllOrderByPriceDesc(10, 0L);
        verify(productRepository, never()).findAllOrderByPriceAsc(anyInt(), anyLong());
        verify(productRepository, never()).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    @DisplayName("Should search product by name when query is provided")
    void getAll_WhenQueryProvided_SearchesByName() {
        String query = "Test";
        when(productRepository.findByNameContainingIgnoreCase(query, pageableAsc))
                .thenReturn(Flux.just(mockProduct));

        StepVerifier.create(productService.getAll(query, pageableAsc))
                .assertNext(result -> assertEquals("Test Product", result.getName()))
                .verifyComplete();

        verify(productRepository).findByNameContainingIgnoreCase(query, pageableAsc);
        verify(productRepository, never()).findAllOrderByPriceAsc(anyInt(), anyLong());
        verify(productRepository, never()).findAllOrderByPriceDesc(anyInt(), anyLong());
    }

    @Test
    @DisplayName("Should return empty page when no products match query")
    void getAll_WhenNoProductsMatch_ReturnsEmptyPage() {
        String query = "None";
        when(productRepository.findByNameContainingIgnoreCase(query, pageableAsc))
                .thenReturn(Flux.empty());

        StepVerifier.create(productService.getAll(query, pageableAsc))
                .verifyComplete();

        verify(productRepository).findByNameContainingIgnoreCase(query, pageableAsc);
    }

    @Test
    @DisplayName("Should return count all products when query null")
    void countAll_WhenQueryIsNull_ReturnsTotalCount() {
        when(productRepository.count()).thenReturn(Mono.just(5L));

        StepVerifier.create(productService.countAll(null))
                .expectNext(5L)
                .verifyComplete();

        verify(productRepository).count();
        verify(productRepository, never()).countByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("Should return count all products when query blank")
    void countAll_WhenQueryIsBlank_ReturnsTotalCount() {
        when(productRepository.count()).thenReturn(Mono.just(5L));

        StepVerifier.create(productService.countAll("   "))
                .expectNext(5L)
                .verifyComplete();

        verify(productRepository).count();
        verify(productRepository, never()).countByNameContainingIgnoreCase(any());
    }
}