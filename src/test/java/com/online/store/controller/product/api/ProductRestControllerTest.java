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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@WebMvcTest(ProductRestController.class)
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    private Product testProduct;
    private ProductDto testProductDto;
    private UUID testUuid;

    @BeforeEach
    void setUp() {
        testUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        testProduct = Product.builder()
                .uuid(testUuid)
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
    void getAll_WithoutQuery_ReturnsAllProducts() throws Exception {

        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Product> productPage = new PageImpl<>(List.of(testProduct));

        when(productService.getAll(eq(null), any(Pageable.class)))
                .thenReturn(productPage);
        when(productMapper.toDto(testProduct))
                .thenReturn(testProductDto);

        mockMvc.perform(get("/api/products")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.content[0].name").value("Test Product"))
                .andExpect(jsonPath("$.content[0].price").value(10));
    }

    @Test
    @DisplayName("GET /api/products?query=Test - should search products")
    void getAll_WithQuery_SearchProducts() throws Exception {

        String searchQuery = "Test";
        PageImpl<Product> productPage = new PageImpl<>(List.of(testProduct));

        when(productService.getAll(eq(searchQuery), any(Pageable.class)))
                .thenReturn(productPage);
        when(productMapper.toDto(testProduct))
                .thenReturn(testProductDto);

        mockMvc.perform(get("/api/products")
                        .param("query", searchQuery)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
    }

    @Test
    @DisplayName("GET /api/products?page=0&size=10 - should return products")
    void getAll_WithPageable_ReturnsProducts() throws Exception {

        PageImpl<Product> productPage = new PageImpl<>(
                List.of(testProduct),
                PageRequest.of(0, 10),
                1
        );

        when(productService.getAll(eq(null), any(Pageable.class)))
                .thenReturn(productPage);
        when(productMapper.toDto(testProduct))
                .thenReturn(testProductDto);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("GET /api/products/{uuid} - should return product with current uuid")
    void getProduct_WithUuid_ReturnsProduct() throws Exception {

        Page<Product> productPage = new PageImpl<>(List.of(testProduct));

        when(productService.getById(testUuid))
                .thenReturn(testProduct);
        when(productMapper.toDto(testProduct))
                .thenReturn(testProductDto);

        mockMvc.perform(get("/api/products/{uuid}", testUuid.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @DisplayName("GET /api/products/{uuid} - should throw ProductNotFoundException when product not found")
    void getProduct_WithNonExistentProduct_ThrowsProductNotFoundException() throws Exception {
        UUID uuid = UUID.randomUUID();

        when(productService.getById(uuid))
                .thenThrow(new ProductNotFoundException(uuid));

        mockMvc.perform(get("/api/products/{uuid}", uuid.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product with this " + uuid + " not found"));
    }
}