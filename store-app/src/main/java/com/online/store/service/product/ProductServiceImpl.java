package com.online.store.service.product;

import com.online.store.exception.product.ProductNotFoundException;
import com.online.store.mapper.product.ProductMapper;
import com.online.store.model.product.Product;
import com.online.store.model.product.ProductCacheDto;
import com.online.store.model.product.ProductCardCacheDto;
import com.online.store.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final String LIST_CACHE_PREFIX = "product:list:";
    private static final String CARD_CACHE_PREFIX = "product:card:";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ReactiveRedisTemplate<String, ProductCacheDto> productListRedisTemplate;
    private final ReactiveRedisTemplate<String, ProductCardCacheDto> productCardRedisTemplate;

    @Value("${cache.product.ttl:PT2M}")
    private Duration cacheTtl;

    @Override
    public Flux<Product> getAll(String query, Pageable pageable) {
        Flux<Product> source;

        if (query != null && !query.isBlank()) {
            return productRepository.findByNameContainingIgnoreCase(query, pageable);
        } else {
            boolean isDesc = pageable.getSort().getOrderFor("price") != null
                             && pageable.getSort().getOrderFor("price").isDescending();
            int limit = pageable.getPageSize();
            long offset = pageable.getOffset();
            source = isDesc
                    ? productRepository.findAllOrderByPriceDesc(limit, offset)
                    : productRepository.findAllOrderByPriceAsc(limit, offset);
        }

        return source.flatMap(this::warmUpListCache);
    }

    @Override
    public Mono<Long> countAll(String query) {
        if (query != null && !query.isBlank()) {
            return productRepository.countByNameContainingIgnoreCase(query);
        }
        return productRepository.count();
    }

    @Override
    public Mono<Product> getById(UUID id) {
        String cardKey = CARD_CACHE_PREFIX + id;
        return productCardRedisTemplate.opsForValue().get(cardKey)
                .map(productMapper::fromCardCacheDto)
                .onErrorResume(e -> {
                    log.warn("Redis недоступен при чтении, загружаем из БД для {}: {}", id, e.getMessage());
                    return Mono.empty();
                })
                .switchIfEmpty(Mono.defer(() -> loadCardFromDbAndCache(id)))
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)));
    }

    private Mono<Product> loadCardFromDbAndCache(UUID id) {
        return productRepository.findById(id)
                .flatMap(product -> cacheCard(product).thenReturn(product));
    }

    private Mono<Product> warmUpListCache(Product product) {
        return cacheList(product).thenReturn(product);
    }

    private Mono<Boolean> cacheList(Product product) {
        String key = LIST_CACHE_PREFIX + product.getProductUuid();
        ProductCacheDto cacheDto = productMapper.toCacheDto(product);
        return productListRedisTemplate.opsForValue()
                .set(key, cacheDto, cacheTtl)
                .onErrorResume(e -> {
                    log.warn("Redis недоступен, кеш списка не обновлён для {}: {}", product.getProductUuid(), e.getMessage());
                    return Mono.just(false);
                });
    }

    private Mono<Boolean> cacheCard(Product product) {
        String key = CARD_CACHE_PREFIX + product.getProductUuid();
        ProductCardCacheDto cacheDto = productMapper.toCardCacheDto(product);
        return productCardRedisTemplate.opsForValue()
                .set(key, cacheDto, cacheTtl)
                .onErrorResume(e -> {
                    log.warn("Redis недоступен, кеш карточки не обновлён для {}: {}", product.getProductUuid(), e.getMessage());
                    return Mono.just(false);
                });
    }
}
