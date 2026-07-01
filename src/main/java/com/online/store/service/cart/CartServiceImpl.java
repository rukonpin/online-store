package com.online.store.service.cart;

import com.online.store.dto.cart.CartDto;
import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.cart.UpdateItemQuantityDto;
import com.online.store.exception.cart.CartItemNotFoundException;
import com.online.store.mapper.cart.CartMapper;
import com.online.store.model.cart.Cart;
import com.online.store.model.cart.CartItem;
import com.online.store.model.product.Product;
import com.online.store.repository.cart.CartItemRepository;
import com.online.store.repository.cart.CartRepository;
import com.online.store.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public Mono<Cart> getOrCreateCart(UUID userUuid) {
        return cartRepository.findByUserUuid(userUuid)
                .switchIfEmpty(Mono.defer(() -> {
                    Cart newCart = Cart.builder()
                            .cartUuid(UUID.randomUUID())
                            .userUuid(userUuid)
                            .items(new ArrayList<>())
                            .build();

                    return cartRepository.save(newCart);
                }))
                .flatMap(cart -> cartItemRepository.findAllByCartUuid(cart.getCartUuid())
                        .collectList()
                        .map(items -> {
                            cart.setItems(items);
                            return cart;
                        }));
    }

    @Override
    @Transactional
    public Mono<Cart> addItem(UUID userUuid, CartItemDto itemDto) {
        return Mono.zip(
                getOrCreateCart(userUuid),
                productService.getById(itemDto.getProductUuid())
        )
                .flatMap(tuple -> {
                    Cart cart = tuple.getT1();
                    Product product = tuple.getT2();

                    CartItem existingItem = cart.getItems().stream()
                            .filter(item -> item.getProductUuid().equals(product.getProductUuid()))
                            .findFirst()
                            .orElse(null);

                    if (existingItem != null) {
                        existingItem.setQuantity(existingItem.getQuantity() + itemDto.getQuantity());
                        return cartItemRepository.save(existingItem)
                                .then(Mono.just(cart));
                    } else {
                        CartItem newItem = CartItem.builder()
                                .itemUuid(UUID.randomUUID())
                                .cartUuid(cart.getCartUuid())
                                .productUuid(product.getProductUuid())
                                .quantity(itemDto.getQuantity())
                                .build();

                        return cartItemRepository.save(newItem)
                                .doOnNext(savedItem -> cart.getItems().add(savedItem))
                                .then(Mono.just(cart));
                    }
                });
    }

    @Override
    @Transactional
    public Mono<Cart> updateItem(UUID userUuid, UUID itemUuid, UpdateItemQuantityDto quantityDto) {
        return getOrCreateCart(userUuid)
                .flatMap(cart -> {
                    CartItem cartItem = cart.getItems().stream()
                            .filter(item -> item.getItemUuid().equals(itemUuid))
                            .findFirst()
                            .orElse(null);

                    if  (cartItem != null) {
                        cartItem.setQuantity(quantityDto.getQuantity());
                        return cartItemRepository.save(cartItem)
                                .then(Mono.just(cart));
                    } else return Mono.error(new CartItemNotFoundException(itemUuid));
                });
    }

    @Override
    @Transactional
    public Mono<Cart> removeItem(UUID userUuid, UUID itemUuid) {
        return getOrCreateCart(userUuid)
                .flatMap(cart -> {
                    boolean removed = cart.getItems()
                            .removeIf(item -> item.getItemUuid().equals(itemUuid));

                    if (!removed) {
                        return Mono.error(new CartItemNotFoundException(itemUuid));
                    }

                    return cartItemRepository.deleteById(itemUuid)
                            .then(Mono.just(cart));
                });
    }

    @Override
    @Transactional
    public Mono<Cart> cleanCart(UUID userUuid) {
        return getOrCreateCart(userUuid)
                .flatMap(cart -> {
                    cart.getItems().clear();
                    return cartItemRepository.deleteAllByCartUuid(cart.getCartUuid())
                            .then(Mono.just(cart));
                });
    }

    @Override
    public Mono<CartDto> toDtoWithProducts(Cart cart) {
        CartDto cartDto = cartMapper.toDto(cart); // верхний уровень маппится как раньше

        return Flux.fromIterable(cart.getItems())
                .flatMapSequential(cartItem -> productService.getById(cartItem.getProductUuid())
                        .map(product -> cartMapper.toDto(cartItem, product)))
                .collectList()
                .doOnNext(cartDto::setItems)
                .thenReturn(cartDto);
    }
}
