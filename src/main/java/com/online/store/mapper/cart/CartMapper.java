package com.online.store.mapper.cart;

import com.online.store.dto.cart.CartDto;
import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.cart.UpdateItemQuantityDto;
import com.online.store.model.cart.Cart;
import com.online.store.model.cart.CartItem;
import com.online.store.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "cartUuid", target = "uuid")
    @Mapping(source = "userUuid", target = "userUuid")
    CartDto toDto(Cart cart);

    @Mapping(source = "cartItem.itemUuid", target = "uuid")
    @Mapping(source = "cartItem.productUuid", target = "productUuid")
    @Mapping(source = "cartItem.quantity", target = "quantity")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imageUrl", target = "imageUrl")
    @Mapping(target = "totalPrice", expression = "java(product.getPrice().multiply(java.math.BigDecimal.valueOf(cartItem.getQuantity())))")
    CartItemDto toDto(CartItem cartItem, Product product);
}
