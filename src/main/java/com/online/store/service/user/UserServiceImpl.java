package com.online.store.service.user;

import com.online.store.dto.user.UserLoginDto;
import com.online.store.dto.user.UserRegistrationDto;
import com.online.store.exception.user.AuthenticationUserException;
import com.online.store.exception.user.UserExistsException;
import com.online.store.exception.ValidationException;
import com.online.store.mapper.user.UserMapper;
import com.online.store.model.user.User;
import com.online.store.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void register(UserRegistrationDto user) {

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new ValidationException("Passwords don't match");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserExistsException(user.getEmail());
        }

        userRepository.save(userMapper.toEntity(user));
    }

    @Override
    public User login(UserLoginDto userLoginDto) {

        User user = userRepository.findByEmail(userLoginDto.getEmail())
                .orElseThrow(() -> new AuthenticationUserException("Invalid email or password"));

        if (!user.getPassword().equals(userLoginDto.getPassword())) {
            throw new AuthenticationUserException("Invalid email or password");
        }

        return user;
    }
}
