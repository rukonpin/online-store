package com.online.store.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginDto {

    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    @NotBlank(message = "Email не может быть пустым")
    private String email;
}
