package com.online.store.mapper.order;

import com.online.store.dto.order.OrderDto;
import com.online.store.dto.order.OrderItemDto;
import com.online.store.model.order.Order;
import com.online.store.model.order.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "order.uuid", target = "uuid")
    @Mapping(source = "user.uuid", target = "userUuid")
    OrderDto toDto(Order order);

    @Mapping(source = "product.uuid", target = "productUuid")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.imageUrl", target = "imageUrl")
    OrderItemDto toDto(OrderItem orderItem);
}
