package com.online.store.service.product;

import com.online.store.exception.ProductNotFoundException;
import com.online.store.model.Product;
import com.online.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product mockProduct;
    private UUID id;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        mockProduct = Product.builder()
                .uuid(id)
                .name("Test Product")
                .price(BigDecimal.TEN)
                .build();
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Should return product when found by UUID")
    void getById_WhenProductExists_ReturnsProduct() {
        when(productRepository.findById(id))
                .thenReturn(Optional.of(mockProduct));

        Product result = productService.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getUuid());
        assertEquals("Test Product", result.getName());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found")
    void getById_WhenProductNotFound_ThrowsException() {
        when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.getById(id));
    }

    @Test
    @DisplayName("Should return all products when query is null")
    void getAll_WhenQueryIsNull_ReturnsAllProducts() {
        Page<Product> expectedPage = new PageImpl<>(List.of(mockProduct));
        when(productRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Product> result = productService.getAll(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findAll(pageable);
        verify(productRepository, never()).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    @DisplayName("Should return all products when query is blank")
    void getAll_WhenQueryIsBlank_ReturnsAllProducts() {
        Page<Product> expectedPage = new PageImpl<>(List.of(mockProduct));
        when(productRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Product> result = productService.getAll("     ", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should search product by name when query is provided")
    void getAll_WhenQueryProvided_SearchesByName() {
        String query = "Test";
        Page<Product> expectedPage = new PageImpl<>(List.of(mockProduct));
        when(productRepository.findByNameContainingIgnoreCase(query, pageable))
                .thenReturn(expectedPage);

        Page<Product> result = productService.getAll(query, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getName());
        verify(productRepository).findByNameContainingIgnoreCase(query, pageable);
        verify(productRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Should return empty page when no products match query")
    void getAll_WhenNoProductsMatch_ReturnsEmptyPage() {
        String query = "None";
        Page<Product> emptyPage = Page.empty();
        when(productRepository.findByNameContainingIgnoreCase(query, pageable))
                .thenReturn(emptyPage);

        Page<Product> result = productService.getAll(query, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findByNameContainingIgnoreCase(query, pageable);
    }
}