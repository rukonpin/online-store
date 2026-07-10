package com.online.store.mapper.product;

import com.online.store.dto.product.ProductDto;
import com.online.store.model.product.Product;
import com.online.store.model.product.ProductCacheDto;
import com.online.store.model.product.ProductCardCacheDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "productUuid", target = "uuid")
    ProductDto toDto(Product product);

    @Mapping(source = "productUuid", target = "uuid")
    ProductCacheDto toCacheDto(Product product);

    @Mapping(source = "productUuid", target = "uuid")
    ProductCardCacheDto toCardCacheDto(Product product);

    @Mapping(source = "uuid", target = "productUuid")
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product fromCacheDto(ProductCacheDto cacheDto);

    @Mapping(source = "uuid", target = "productUuid")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product fromCardCacheDto(ProductCardCacheDto cardCacheDto);
}
