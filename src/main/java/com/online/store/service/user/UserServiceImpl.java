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
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Mono<Void> register(UserRegistrationDto user) {

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            return Mono.error(new ValidationException("Passwords don't match"));
        }

        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new UserExistsException(user.getEmail()));
                    }
                    User newUser = userMapper.toEntity(user);
                    newUser.setUserUuid(UUID.randomUUID());
                    return userRepository.save(newUser).then();
                });
    }

    @Override
    public Mono<User> login(UserLoginDto userLoginDto) {

        return userRepository.findByEmail(userLoginDto.getEmail())
                .filter(u -> u.getPassword().equals(userLoginDto.getPassword()))
                .switchIfEmpty(Mono.error(new AuthenticationUserException("Invalid email or password")));
    }
}
