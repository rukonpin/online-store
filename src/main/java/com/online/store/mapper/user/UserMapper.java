package com.online.store.mapper.user;

import com.online.store.dto.user.UserRegistrationDto;
import com.online.store.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Для сохранения в базу
    User toEntity(UserRegistrationDto userRegistrationDto);

    // Для ответа клиенту
    @Mapping(target = "password", ignore = true)
    UserRegistrationDto toDto(User user);

}
