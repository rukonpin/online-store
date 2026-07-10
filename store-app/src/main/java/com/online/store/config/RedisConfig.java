package com.online.store.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.online.store.model.product.ProductCacheDto;
import com.online.store.model.product.ProductCardCacheDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    public ReactiveRedisTemplate<String, ProductCacheDto> productListRedisTemplate(
            ReactiveRedisConnectionFactory factory,
            ObjectMapper redisObjectMapper) {

        Jackson2JsonRedisSerializer<ProductCacheDto> serializer =
                new Jackson2JsonRedisSerializer<>(redisObjectMapper, ProductCacheDto.class);

        RedisSerializationContext<String, ProductCacheDto> context =
                RedisSerializationContext.<String, ProductCacheDto>newSerializationContext(new StringRedisSerializer())
                        .value(serializer)
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, ProductCardCacheDto> productCardRedisTemplate(
            ReactiveRedisConnectionFactory factory,
            ObjectMapper redisObjectMapper) {

        Jackson2JsonRedisSerializer<ProductCardCacheDto> serializer =
                new Jackson2JsonRedisSerializer<>(redisObjectMapper, ProductCardCacheDto.class);

        RedisSerializationContext<String, ProductCardCacheDto> context =
                RedisSerializationContext.<String, ProductCardCacheDto>newSerializationContext(new StringRedisSerializer())
                        .value(serializer)
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
