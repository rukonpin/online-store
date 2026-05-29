package com.online.store.controller.cart.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.store.dto.cart.CartDto;
import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.cart.UpdateItemQuantityDto;
import com.online.store.exception.cart.CartItemNotFoundException;
import com.online.store.exception.product.ProductNotFoundException;
import com.online.store.mapper.cart.CartMapper;
import com.online.store.model.cart.Cart;
import com.online.store.model.cart.CartItem;
import com.online.store.model.product.Product;
import com.online.store.model.user.User;
import com.online.store.service.cart.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartRestController.class)
class CartRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private CartMapper cartMapper;

    @Test
    @DisplayName("GET /api/cart - should return cart when user is authenticated")
    void getCart_WhenAuthorized_ReturnsCart() throws Exception {
        // Given
        UUID userUuid = UUID.randomUUID();
        User mockUser = User.builder().uuid(userUuid).build();
        Cart mockCart = Cart.builder().user(mockUser).build();

        CartDto mockCartDto = CartDto.builder()
                .uuid(UUID.randomUUID())
                .userUuid(userUuid)
                .items(List.of())
                .build();

        when(cartService.getOrCreateCart(eq(userUuid)))
                .thenReturn(mockCart);
        when(cartMapper.toDto(mockCart))
                .thenReturn(mockCartDto);

        // When & Then
        mockMvc.perform(get("/api/cart")
                        .sessionAttr("user_id", userUuid)
                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userUuid").value(userUuid.toString()))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/cart - should return 401 Unauthorized when user is not authenticated")
    void getCart_WhenNotAuthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/cart")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/cart/items - should add item to cart when request is valid")
    void addItemToCart_WhenValidRequest_ReturnsCart() throws Exception {
        UUID userUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();
        UUID productUuid = UUID.randomUUID();
        UUID cartUuid = UUID.randomUUID();

        // то, что отправляет клиент
        CartItemDto itemRequestDto = CartItemDto.builder()
                .productUuid(productUuid)
                .quantity(1)
                .build();

        User mockUser = User.builder().uuid(userUuid).build();
        Product mockProduct = Product.builder().uuid(productUuid).build();

        CartItem cartItem = CartItem.builder()
                .uuid(itemUuid)
                .product(mockProduct)
                .quantity(1)
                .build();

        List<CartItem> itemsInCart = new ArrayList<>();
        itemsInCart.add(cartItem);

        Cart mockCart = Cart.builder()
                .uuid(cartUuid)
                .user(mockUser)
                .items(itemsInCart)
                .build();

        // то, во что маппер превратит сущность
        CartItemDto itemResponseDto = CartItemDto.builder()
                .uuid(itemUuid)
                .productUuid(productUuid)
                .quantity(1)
                .build();

        CartDto mockCartDto = CartDto.builder()
                .uuid(mockCart.getUuid())
                .userUuid(userUuid)
                .items(List.of(itemResponseDto))
                .build();

        when(cartService.addItem(eq(userUuid), any(CartItemDto.class)))
                .thenReturn(mockCart);
        when(cartMapper.toDto(mockCart))
                .thenReturn(mockCartDto);

        mockMvc.perform(post("/api/cart/items")
                .sessionAttr("user_id", userUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequestDto)))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(cartUuid.toString()))
                .andExpect(jsonPath("$.userUuid").value(userUuid.toString()))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].uuid").value(itemUuid.toString()))
                .andExpect(jsonPath("$.items[0].productUuid").value(productUuid.toString()));
    }

    @Test
    @DisplayName("POST /api/cart/items - should throw ProductNotFoundException when product does not exist")
    void addItemToCart_WhenProductNotFound_ReturnsStatusNotFound() throws Exception {
        UUID userUuid = UUID.randomUUID();
        UUID productUuid = UUID.randomUUID();

        CartItemDto itemRequestDto = CartItemDto.builder()
                .productUuid(productUuid)
                .quantity(1)
                .build();

        when(cartService.addItem(eq(userUuid), any(CartItemDto.class)))
                .thenThrow(new ProductNotFoundException(productUuid));

        mockMvc.perform(post("/api/cart/items")
                        .sessionAttr("user_id", userUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
//                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Product with this " + productUuid + " not found"));
    }

    @Test
    @DisplayName("PUT /api/cart/items/{itemUuid} - should update item quantity when item exists")
    void updateItem_WhenItemExists_ReturnsUpdatedCart() throws Exception {
        UUID userUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();
        UUID productUuid = UUID.randomUUID();
        UUID cartUuid = UUID.randomUUID();

        UpdateItemQuantityDto quantity =  new UpdateItemQuantityDto();
        quantity.setQuantity(2);

        User mockUser = User.builder().uuid(userUuid).build();
        Product mockProduct = Product.builder().uuid(productUuid).build();

        CartItem cartItem = CartItem.builder()
                .uuid(itemUuid)
                .product(mockProduct)
                .quantity(quantity.getQuantity())
                .build();

        List<CartItem> itemsInCart = new ArrayList<>();
        itemsInCart.add(cartItem);

        Cart mockCart = Cart.builder()
                .uuid(cartUuid)
                .user(mockUser)
                .items(itemsInCart)
                .build();

        CartItemDto itemResponseDto = CartItemDto.builder()
                .uuid(itemUuid)
                .productUuid(productUuid)
                .quantity(quantity.getQuantity())
                .totalPrice(BigDecimal.TEN)
                .build();

        CartDto mockCartDto = CartDto.builder()
                .uuid(mockCart.getUuid())
                .userUuid(userUuid)
                .items(List.of(itemResponseDto))
                .build();

        when(cartService.updateItem(eq(userUuid), eq(itemUuid), any(UpdateItemQuantityDto.class)))
                .thenReturn(mockCart);
        when(cartMapper.toDto(mockCart))
                .thenReturn(mockCartDto);

        mockMvc.perform(put("/api/cart/items/{itemUuid}", itemUuid.toString())
                        .sessionAttr("user_id", userUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quantity)))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(quantity.getQuantity()));

    }

    @Test
    @DisplayName("PUT /api/cart/items/{itemUuid} - should throw CartItemNotFoundException when item does not exist")
    void updateItem_WhenItemNotFound_ReturnsStatusNotFound() throws Exception {
        UUID userUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();

        UpdateItemQuantityDto quantity =  new UpdateItemQuantityDto();
        quantity.setQuantity(2);

        when(cartService.updateItem(eq(userUuid), eq(itemUuid), any(UpdateItemQuantityDto.class)))
                .thenThrow(new CartItemNotFoundException(itemUuid));

        mockMvc.perform(put("/api/cart/items/{itemUuid}", itemUuid.toString())
                        .sessionAttr("user_id", userUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quantity)))
//                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Could not find item with uuid " + itemUuid));
    }

    @Test
    @DisplayName("DELETE /api/cart/items/{itemUuid} - should remove item from cart when item exists")
    void deleteItem_WhenItemFound_ReturnsCartDto() throws Exception {
        UUID userUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();

        User mockUser = User.builder().uuid(userUuid).build();

        Cart mockCart = Cart.builder()
                .uuid(UUID.randomUUID())
                .user(mockUser)
                .items(new ArrayList<>())
                .build();

        CartDto mockCartDto = CartDto.builder()
                .uuid(mockCart.getUuid())
                .userUuid(userUuid)
                .items(List.of())
                .build();

        when(cartService.removeItem(userUuid, itemUuid)).thenReturn(mockCart);
        when(cartMapper.toDto(mockCart)).thenReturn(mockCartDto);

        mockMvc.perform(delete("/api/cart/items/{itemUuid}", itemUuid.toString())
                        .sessionAttr("user_id", userUuid)
                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(mockCartDto.getUuid().toString()))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    @DisplayName("DELETE /api/cart/items/{itemUuid} - should throw CartItemNotFoundException when item does not exist")
    void deleteItem_WhenItemNotFound_ReturnsStatusNotFound() throws Exception {
        UUID userUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();

        when(cartService.removeItem(userUuid, itemUuid))
                .thenThrow(new CartItemNotFoundException(itemUuid));

        mockMvc.perform(delete("/api/cart/items/{itemUuid}", itemUuid.toString())
                        .sessionAttr("user_id", userUuid)
                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Could not find item with uuid " + itemUuid));
    }

    @Test
    @DisplayName("DELETE /api/cart - should clear all items from cart")
    void cleanCart_WhenCartNotEmpty_ReturnsEmptyCart() throws Exception {
        UUID userUuid = UUID.randomUUID();

        User mockUser = User.builder().uuid(userUuid).build();

        Cart mockCart = Cart.builder()
                .uuid(UUID.randomUUID())
                .user(mockUser)
                .items(new ArrayList<>())
                .build();

        CartDto mockCartDto = CartDto.builder()
                .uuid(mockCart.getUuid())
                .userUuid(userUuid)
                .items(List.of())
                .build();

        when(cartService.cleanCart(userUuid)).thenReturn(mockCart);
        when(cartMapper.toDto(mockCart)).thenReturn(mockCartDto);

        mockMvc.perform(delete("/api/cart")
                        .sessionAttr("user_id", userUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(mockCartDto.getUuid().toString()))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }
}