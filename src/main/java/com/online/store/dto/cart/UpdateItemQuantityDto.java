package com.online.store.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class UpdateItemQuantityDto {
    @NotNull(message = "Количество не может быть пустым")
    @Min(value = 1, message = "Количество должно быть не менее 1")
    private Integer quantity;
}
