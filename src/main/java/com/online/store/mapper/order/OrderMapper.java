package com.online.store.mapper.order;

import com.online.store.dto.order.OrderDto;
import com.online.store.dto.order.OrderItemDto;
import com.online.store.model.order.Order;
import com.online.store.model.order.OrderItem;
import com.online.store.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "orderUuid", target = "uuid")
    @Mapping(source = "userUuid", target = "userUuid")
    OrderDto toDto(Order order);

    @Mapping(source = "orderItem.itemUuid", target = "uuid")
    @Mapping(source = "orderItem.productUuid", target = "productUuid")
    @Mapping(source = "orderItem.priceAtPurchase", target = "priceAtPurchase")
    @Mapping(source = "orderItem.quantity", target = "quantity")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imageUrl", target = "imageUrl")
    OrderItemDto toDto(OrderItem orderItem, Product product);
}
