package com.online.store.mapper.product;

import com.online.store.dto.product.ProductDto;
import com.online.store.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Entity -> DTO для списка
    @Mapping(source = "uuid", target = "uuid")
    ProductDto toDto(Product product);
}
