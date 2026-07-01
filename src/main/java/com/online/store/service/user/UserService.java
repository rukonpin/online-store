package com.online.store.service.user;

import com.online.store.dto.user.UserLoginDto;
import com.online.store.dto.user.UserRegistrationDto;
import com.online.store.model.user.User;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<Void> register(UserRegistrationDto user);
    Mono<User> login(UserLoginDto userLoginDto);
}
