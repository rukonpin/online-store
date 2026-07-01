package com.online.store.mapper.product;

import com.online.store.dto.product.ProductDto;
import com.online.store.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "productUuid", target = "uuid")
    ProductDto toDto(Product product);
}
