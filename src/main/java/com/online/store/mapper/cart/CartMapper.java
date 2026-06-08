package com.online.store.mapper.cart;

import com.online.store.dto.cart.CartDto;
import com.online.store.dto.cart.CartItemDto;
import com.online.store.dto.cart.UpdateItemQuantityDto;
import com.online.store.model.cart.Cart;
import com.online.store.model.cart.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "cart.uuid", target = "uuid")
    @Mapping(source = "user.uuid", target = "userUuid")
    CartDto toDto(Cart cart);

    @Mapping(source = "product.uuid", target = "productUuid")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imageUrl", target = "imageUrl")
    CartItemDto toDto(CartItem cartItem);
}
