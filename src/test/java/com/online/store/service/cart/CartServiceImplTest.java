package com.online.store.service.cart;

import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.cart.UpdateItemQuantityDto;
import com.online.store.exception.cart.CartItemNotFoundException;
import com.online.store.model.cart.Cart;
import com.online.store.model.cart.CartItem;
import com.online.store.model.product.Product;
import com.online.store.repository.cart.CartItemRepository;
import com.online.store.repository.cart.CartRepository;
import com.online.store.service.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    @DisplayName("Should return cart when exists user")
    void getOrCreateCart_WhenUserExists_ReturnsExistingCart() {
        UUID userUuid = UUID.randomUUID();
        UUID cartUuid = UUID.randomUUID();
        Cart mockCart = Cart.builder()
                .cartUuid(cartUuid)
                .userUuid(userUuid)
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Mono.just(mockCart));
        when(cartItemRepository.findAllByCartUuid(mockCart.getCartUuid()))
                .thenReturn(Flux.empty());

        StepVerifier.create(cartService.getOrCreateCart(userUuid))
                        .assertNext(cart -> {
                            assertEquals(userUuid, cart.getUserUuid());
                            assertEquals(cartUuid, cart.getCartUuid());
                            assertTrue(cart.getItems().isEmpty());
                        })
                        .verifyComplete();

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should return new cart when the user does not yet have a cart")
    void getOrCreateCart_WhenUserDoesNotHaveCart_ReturnsNewCart() {
        UUID userUuid = UUID.randomUUID();
        UUID cartUuid = UUID.randomUUID();
        Cart mockCart = Cart.builder()
                .cartUuid(cartUuid)
                .userUuid(userUuid)
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Mono.empty());
        when(cartRepository.save(any(Cart.class)))
                .thenReturn(Mono.just(mockCart));
        when(cartItemRepository.findAllByCartUuid(mockCart.getCartUuid()))
                .thenReturn(Flux.empty());

        StepVerifier.create(cartService.getOrCreateCart(userUuid))
                .assertNext(cart -> {
                    assertEquals(userUuid, cart.getUserUuid());
                    assertEquals(cartUuid, cart.getCartUuid());
                    assertTrue(cart.getItems().isEmpty());
                })
                .verifyComplete();

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should add item when empty cart")
    void addItem_WhenEmptyCart_AddNewItemToCart() {
        UUID userUuid = UUID.randomUUID();
        UUID productUuid = UUID.randomUUID();
        UUID cartUuid = UUID.randomUUID();
        Integer expectedQuantity = 1;

        Cart mockCart = Cart.builder()
                .cartUuid(cartUuid)
                .userUuid(userUuid)
                .items(new ArrayList<>())
                .build();

        Product mockProduct = Product.builder()
                .productUuid(productUuid)
                .name("iPhone 16")
                .price(BigDecimal.valueOf(2000))
                .build();

        CartItemDto mockCartItem = CartItemDto.builder()
                .productUuid(mockProduct.getProductUuid())
                .quantity(expectedQuantity)
                .build();

        CartItem savedCartItem = CartItem.builder()
                .itemUuid(UUID.randomUUID())
                .cartUuid(cartUuid)
                .productUuid(productUuid)
                .quantity(expectedQuantity)
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Mono.just(mockCart));
        when(cartItemRepository.findAllByCartUuid(mockCart.getCartUuid()))
                .thenReturn(Flux.empty());
        when(productService.getById(mockCartItem.getProductUuid()))
                .thenReturn(Mono.just(mockProduct));
        when(cartItemRepository.save(any(CartItem.class)))
                .thenReturn(Mono.just(savedCartItem));

        StepVerifier.create(cartService.addItem(userUuid, mockCartItem))
                .assertNext(cart -> {
                    assertEquals(1, cart.getItems().size());
                    CartItem item = cart.getItems().getFirst();
                    assertEquals(productUuid, item.getProductUuid());
                    assertEquals(expectedQuantity, item.getQuantity());
                })
                .verifyComplete();

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should add item when there are already items in the cart")
    void addItem_WhenCartNotEmpty_AddNewItemToCart() {
        UUID userUuid = UUID.randomUUID();
        UUID cartUuid = UUID.randomUUID();
        UUID existingProductUuid = UUID.randomUUID();
        UUID newProductUuid = UUID.randomUUID();
        Integer expectedQuantity = 1;

        CartItem existingCartItem = CartItem.builder()
                .itemUuid(UUID.randomUUID())
                .cartUuid(cartUuid)
                .productUuid(existingProductUuid)
                .quantity(2)
                .build();

        List<CartItem> itemsInCart = new ArrayList<>();
        itemsInCart.add(existingCartItem);

        Cart mockCart = Cart.builder()
                .cartUuid(cartUuid)
                .userUuid(userUuid)
                .items(itemsInCart)
                .build();

        Product mockProduct = Product.builder().productUuid(newProductUuid).build();
        CartItemDto mockCartItemDto = CartItemDto.builder()
                .productUuid(mockProduct.getProductUuid())
                .quantity(expectedQuantity)
                .build();

        CartItem savedNewItem = CartItem.builder()
                .itemUuid(UUID.randomUUID())
                .cartUuid(cartUuid)
                .productUuid(newProductUuid)
                .quantity(expectedQuantity)
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Mono.just(mockCart));
        when(cartItemRepository.findAllByCartUuid(cartUuid))
                .thenReturn(Flux.fromIterable(itemsInCart));
        when(productService.getById(mockCartItemDto.getProductUuid()))
                .thenReturn(Mono.just(mockProduct));
        when(cartItemRepository.save(any(CartItem.class)))
                .thenReturn(Mono.just(savedNewItem));

        StepVerifier.create(cartService.addItem(userUuid, mockCartItemDto))
                .assertNext(cart -> {
                    assertEquals(2, cart.getItems().size());

                    CartItem addedItem = cart.getItems().stream()
                            .filter(item -> item.getProductUuid().equals(newProductUuid))
                            .findFirst()
                            .orElse(null);

                    assertNotNull(addedItem);
                    assertEquals(expectedQuantity, addedItem.getQuantity());
                })
                .verifyComplete();

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should update item when item found by item uuid")
    void updateItem_WhenItemUuidExists_ReturnCorrectQuantity() {
        UUID userUuid = UUID.randomUUID();
        UUID cartUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();
        UUID existingProductUuid = UUID.randomUUID();

        UpdateItemQuantityDto expectedQuantity = new UpdateItemQuantityDto();
        expectedQuantity.setQuantity(2);

        CartItem existingCartItem = CartItem.builder()
                .itemUuid(itemUuid)
                .cartUuid(cartUuid)
                .productUuid(existingProductUuid)
                .quantity(1)
                .build();

        List<CartItem> itemsInCart = new ArrayList<>();
        itemsInCart.add(existingCartItem);

        Cart mockCart = Cart.builder()
                .cartUuid(cartUuid)
                .userUuid(userUuid)
                .items(itemsInCart)
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Mono.just(mockCart));
        when(cartItemRepository.findAllByCartUuid(cartUuid))
                .thenReturn(Flux.fromIterable(itemsInCart));
        when(cartItemRepository.save(any(CartItem.class)))
                .thenReturn(Mono.just(existingCartItem));

        StepVerifier.create(cartService.updateItem(userUuid, itemUuid, expectedQuantity))
                .assertNext(cart -> {
                    assertEquals(1, cart.getItems().size());

                    CartItem updatedItem = cart.getItems().stream()
                            .filter(item -> item.getItemUuid().equals(itemUuid))
                            .findFirst()
                            .orElse(null);

                    assertNotNull(updatedItem);
                    assertEquals(2, updatedItem.getQuantity());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw CartItemNotFoundException when item not found")
    void updateItem_WhenItemNotFound_ThrowCartItemNotFoundException() {
        UUID userUuid = UUID.randomUUID();
        UUID cartUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();

        UpdateItemQuantityDto expectedQuantity = new UpdateItemQuantityDto();
        expectedQuantity.setQuantity(2);

        Cart mockCart = Cart.builder()
                .cartUuid(cartUuid)
                .userUuid(userUuid)
                .items(new ArrayList<>())
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Mono.just(mockCart));
        when(cartItemRepository.findAllByCartUuid(cartUuid))
                .thenReturn(Flux.empty());

        StepVerifier.create(cartService.updateItem(userUuid, itemUuid, expectedQuantity))
                .expectError(CartItemNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw CartItemNotFoundException when item not found")
    void removeItem_WhenItemNotFound_ThrowCartItemNotFoundException() {
        UUID userUuid = UUID.randomUUID();
        UUID cartUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();

        Cart mockCart = Cart.builder()
                .cartUuid(cartUuid)
                .userUuid(userUuid)
                .items(new ArrayList<>())
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Mono.just(mockCart));
        when(cartItemRepository.findAllByCartUuid(cartUuid))
                .thenReturn(Flux.empty());

        StepVerifier.create(cartService.removeItem(userUuid, itemUuid))
                .expectError(CartItemNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should remove item from cart and return updated cart when item found")
    void removeItem_WhenItemFound_RemovesItemAndReturnsUpdatedCart() {
        UUID userUuid = UUID.randomUUID();
        UUID cartUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();

        CartItem existingCartItem = CartItem.builder()
                .itemUuid(itemUuid)
                .cartUuid(cartUuid)
                .quantity(3)
                .build();

        List<CartItem> itemsInCart = new ArrayList<>();
        itemsInCart.add(existingCartItem);

        Cart mockCart = Cart.builder()
                .cartUuid(cartUuid)
                .userUuid(userUuid)
                .items(itemsInCart)
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Mono.just(mockCart));
        when(cartItemRepository.findAllByCartUuid(cartUuid))
                .thenReturn(Flux.fromIterable(itemsInCart));
        when(cartItemRepository.deleteById(itemUuid))
                .thenReturn(Mono.empty());

        StepVerifier.create(cartService.removeItem(userUuid, itemUuid))
                .assertNext(cart -> assertEquals(0, cart.getItems().size()))
                .verifyComplete();

        verify(cartRepository).findByUserUuid(userUuid);
    }

    @Test
    @DisplayName("Should clean cart when cart not empty")
    void cleanCart_WhenCartNotEmpty_ReturnEmptyCart() {
        UUID userUuid = UUID.randomUUID();
        UUID cartUuid = UUID.randomUUID();

        CartItem item1 = CartItem.builder().itemUuid(UUID.randomUUID()).cartUuid(cartUuid).quantity(1).build();
        CartItem item2 = CartItem.builder().itemUuid(UUID.randomUUID()).cartUuid(cartUuid).quantity(2).build();
        CartItem item3 = CartItem.builder().itemUuid(UUID.randomUUID()).cartUuid(cartUuid).quantity(3).build();

        List<CartItem> itemsInCart = new ArrayList<>(List.of(item1, item2, item3));

        Cart mockCart = Cart.builder()
                .cartUuid(cartUuid)
                .userUuid(userUuid)
                .items(itemsInCart)
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Mono.just(mockCart));
        when(cartItemRepository.findAllByCartUuid(cartUuid))
                .thenReturn(Flux.fromIterable(itemsInCart));
        when(cartItemRepository.deleteAllByCartUuid(cartUuid))
                .thenReturn(Mono.empty());

        StepVerifier.create(cartService.cleanCart(userUuid))
                .assertNext(cart -> assertEquals(0, cart.getItems().size()))
                .verifyComplete();

        verify(cartRepository).findByUserUuid(userUuid);
    }
}