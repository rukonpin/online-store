package com.online.store.service.cart;

import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.cart.UpdateItemQuantityDto;
import com.online.store.exception.cart.CartItemNotFoundException;
import com.online.store.model.cart.Cart;
import com.online.store.model.cart.CartItem;
import com.online.store.model.product.Product;
import com.online.store.model.user.User;
import com.online.store.repository.cart.CartRepository;
import com.online.store.service.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    @DisplayName("Should return cart when exists user")
    void getOrCreateCart_WhenUserExists_ReturnsExistingCart() {

        UUID userUuid = UUID.randomUUID();
        User mockUser = User.builder().uuid(userUuid).build();
        Cart mockCart = Cart.builder().user(mockUser).build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Optional.of(mockCart));

        Cart result = cartService.getOrCreateCart(userUuid);

        assertNotNull(result);
        assertEquals(mockCart, result);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should return new cart when the user does not yet have a cart")
    void getOrCreateCart_WhenUserDoesNotHaveCart_ReturnsNewCart() {

        UUID userUuid = UUID.randomUUID();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(i -> i.getArgument(0));


        Cart result = cartService.getOrCreateCart(userUuid);

        assertNotNull(result);
        assertEquals(userUuid, result.getUser().getUuid());
        assertTrue(result.getItems().isEmpty());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should add item when empty cart")
    void addItem_WhenEmptyCart_AddNewItemToCart() {

        UUID userUuid = UUID.randomUUID();
        UUID productUuid = UUID.randomUUID();
        Integer expectedQuantity = 1;

        User mockUser = User.builder().uuid(userUuid).build();
        Cart mockCart = Cart.builder()
                .user(mockUser)
                .items(new ArrayList<>())
                .build();

        Product mockProduct = Product.builder().uuid(productUuid).build();
        CartItemDto mockCartItem = CartItemDto.builder()
                .productUuid(mockProduct.getUuid())
                .quantity(expectedQuantity)
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Optional.of(mockCart));
        when(productService.getById(mockCartItem.getProductUuid()))
                .thenReturn(mockProduct);

        Cart result = cartService.addItem(userUuid, mockCartItem);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(productUuid, result.getItems().getFirst().getProduct().getUuid());
        assertEquals(expectedQuantity, result.getItems().getFirst().getQuantity());
        assertEquals(result, result.getItems().getFirst().getCart());
    }

    @Test
    @DisplayName("Should add item when there are already items in the cart")
    void addItem_WhenCartNotEmpty_AddNewItemToCart() {

        UUID userUuid = UUID.randomUUID();
        UUID existingProductUuid = UUID.randomUUID();
        UUID newProductUuid = UUID.randomUUID();
        Integer expectedQuantity = 1;

        User mockUser = User.builder().uuid(userUuid).build();

        Product existingProduct = Product.builder().uuid(existingProductUuid).build();
        CartItem existingCartItem = CartItem.builder()
                .product(existingProduct)
                .quantity(2)
                .build();

        List<CartItem> itemsInCart = new ArrayList<>();
        itemsInCart.add(existingCartItem);

        Cart mockCart = Cart.builder()
                .user(mockUser)
                .items(itemsInCart)
                .build();

        Product mockProduct = Product.builder().uuid(newProductUuid).build();
        CartItemDto mockCartItemDto = CartItemDto.builder()
                .productUuid(mockProduct.getUuid())
                .quantity(expectedQuantity)
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Optional.of(mockCart));
        when(productService.getById(mockCartItemDto.getProductUuid()))
                .thenReturn(mockProduct);

        Cart result = cartService.addItem(userUuid, mockCartItemDto);

        assertNotNull(result);
        assertEquals(2, result.getItems().size());

        CartItem addedItem = result.getItems().stream()
                .filter(item -> item.getProduct().getUuid().equals(newProductUuid))
                .findFirst()
                .orElse(null);

        assertNotNull(addedItem);
        assertEquals(expectedQuantity, addedItem.getQuantity());
        assertEquals(result, addedItem.getCart());
    }

    @Test
    @DisplayName("Should update item when item found by item uuid")
    void updateItem_WhenItemUuidExists_ReturnCorrectQuantity() {

        UUID userUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();
        UUID existingProductUuid = UUID.randomUUID();

        UpdateItemQuantityDto expectedQuantity = new UpdateItemQuantityDto();
        expectedQuantity.setQuantity(2);

        User mockUser = User.builder().uuid(userUuid).build();

        Product existingProduct = Product.builder().uuid(existingProductUuid).build();
        CartItem existingCartItem = CartItem.builder()
                .uuid(itemUuid)
                .product(existingProduct)
                .quantity(1)
                .build();

        List<CartItem> itemsInCart = new ArrayList<>();
        itemsInCart.add(existingCartItem);

        Cart mockCart = Cart.builder()
                .user(mockUser)
                .items(itemsInCart)
                .build();

        existingCartItem.setCart(mockCart);

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Optional.of(mockCart));

        Cart result = cartService.updateItem(userUuid, itemUuid, expectedQuantity);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());

        CartItem addedItem = result.getItems().stream()
                .filter(item -> item.getUuid().equals(itemUuid))
                .findFirst()
                .orElse(null);

        assertNotNull(addedItem);
        assertEquals(2, addedItem.getQuantity());
        assertEquals(result, addedItem.getCart());
    }

    @Test
    @DisplayName("Should throw CartItemNotFoundException when item not found")
    void updateItem_WhenItemNotFound_ThrowCartItemNotFoundException() {

        UUID userUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();

        UpdateItemQuantityDto expectedQuantity = new UpdateItemQuantityDto();
        expectedQuantity.setQuantity(2);

        User mockUser = User.builder().uuid(userUuid).build();

        Cart mockCart = Cart.builder()
                .user(mockUser)
                .items(new ArrayList<>())
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Optional.of(mockCart));

        assertThrows(CartItemNotFoundException.class,
                () -> cartService.updateItem(userUuid, itemUuid, expectedQuantity));
    }

    @Test
    @DisplayName("Should throw CartItemNotFoundException when item not found")
    void removeItem_WhenItemNotFound_ThrowCartItemNotFoundException() {

        UUID userUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();

        User mockUser = User.builder().uuid(userUuid).build();

        Cart mockCart = Cart.builder()
                .user(mockUser)
                .items(new ArrayList<>())
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Optional.of(mockCart));

        assertThrows(CartItemNotFoundException.class, () -> cartService.removeItem(userUuid, itemUuid));
    }

    @Test
    @DisplayName("Should remove item from cart and return updated cart when item found")
    void removeItem_WhenItemFound_RemovesItemAndReturnsUpdatedCart() {

        UUID userUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();

        User mockUser = User.builder().uuid(userUuid).build();

        CartItem existingCartItem = CartItem.builder()
                .uuid(itemUuid)
                .quantity(3)
                .build();

        List<CartItem> itemsInCart = new ArrayList<>();
        itemsInCart.add(existingCartItem);

        Cart mockCart = Cart.builder()
                .user(mockUser)
                .items(itemsInCart)
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Optional.of(mockCart));

        Cart result = cartService.removeItem(userUuid, itemUuid);

        assertNotNull(result);
        assertEquals(0, result.getItems().size());
        verify(cartRepository).findByUserUuid(userUuid);
    }

    @Test
    @DisplayName("Should clean cart when cart not empty")
    void cleanCart_WhenCartNotEmpty_ReturnEmptyCart() {

        UUID userUuid = UUID.randomUUID();
        User mockUser = User.builder().uuid(userUuid).build();

        CartItem item1 = CartItem.builder().quantity(1).build();
        CartItem item2 = CartItem.builder().quantity(2).build();
        CartItem item3 = CartItem.builder().quantity(3).build();

        List<CartItem> itemsInCart = new ArrayList<>(List.of(item1, item2, item3));

        Cart mockCart = Cart.builder()
                .user(mockUser)
                .items(itemsInCart)
                .build();

        when(cartRepository.findByUserUuid(userUuid))
                .thenReturn(Optional.of(mockCart));

        Cart result = cartService.cleanCart(userUuid);

        assertNotNull(result);
        assertEquals(0, result.getItems().size());
        verify(cartRepository).findByUserUuid(userUuid);
    }
}