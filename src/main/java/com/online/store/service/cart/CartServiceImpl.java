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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    @Override
    @Transactional
    public Cart getOrCreateCart(UUID userUuid) {
        return cartRepository.findByUserUuid(userUuid)
                .orElseGet(() -> {
                    User userReference = User.builder()
                            .uuid(userUuid)
                            .build();

                    Cart newCart =  Cart.builder()
                            .user(userReference)
                            .items(new ArrayList<>())
                            .build();

                    return cartRepository.save(newCart);
                });
    }

    @Override
    @Transactional
    public Cart addItem(UUID userUuid, CartItemDto itemDto) {
        Cart cart = getOrCreateCart(userUuid);
        Product product = productService.getById(itemDto.getProductUuid());

        cart.getItems().stream()
                .filter(cartItem -> cartItem.getProduct().getUuid().equals(product.getUuid()))
                .findFirst()
                .ifPresentOrElse(
                        existingItem -> existingItem.setQuantity(
                                existingItem.getQuantity() + itemDto.getQuantity()),
                        () -> {
                            CartItem newCartItem = CartItem.builder()
                                    .cart(cart)
                                    .product(product)
                                    .quantity(itemDto.getQuantity())
                                    .build();

                            cart.getItems().add(newCartItem);
                        }
                );

        return cart;
    }

    @Override
    @Transactional
    public Cart updateItem(UUID userUuid, UUID itemUuid, UpdateItemQuantityDto quantityDto) {
        Cart cart = getOrCreateCart(userUuid);

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getUuid().equals(itemUuid))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(itemUuid));

        cartItem.setQuantity(quantityDto.getQuantity());

        return cart;
    }

    @Override
    @Transactional
    public Cart removeItem(UUID userUuid, UUID itemUuid) {
        Cart cart = getOrCreateCart(userUuid);;

        boolean removed = cart.getItems()
                .removeIf(item -> item.getUuid().equals(itemUuid));

        if (!removed) {
            throw new CartItemNotFoundException(itemUuid);
        }

        return cart;
    }

    @Override
    @Transactional
    public Cart cleanCart(UUID userUuid) {
        Cart cart = getOrCreateCart(userUuid);
        cart.getItems().clear();
        return cart;
    }
}
