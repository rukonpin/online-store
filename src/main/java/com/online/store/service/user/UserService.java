package com.online.store.service.user;

import com.online.store.dto.user.UserLoginDto;
import com.online.store.dto.user.UserRegistrationDto;
import com.online.store.model.user.User;

public interface UserService {

    void register(UserRegistrationDto user);
    User login(UserLoginDto userLoginDto);
}
