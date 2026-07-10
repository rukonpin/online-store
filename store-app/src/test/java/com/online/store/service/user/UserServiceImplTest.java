package com.online.store.service.user;

import com.online.store.dto.user.UserLoginDto;
import com.online.store.dto.user.UserRegistrationDto;
import com.online.store.exception.user.AuthenticationUserException;
import com.online.store.exception.user.UserExistsException;
import com.online.store.exception.ValidationException;
import com.online.store.mapper.user.UserMapper;
import com.online.store.model.user.User;
import com.online.store.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDto mockUserDto;
    private UserLoginDto mockUserLoginDto;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUserDto = UserRegistrationDto.builder()
                .username("TestUser")
                .email("test@gmail.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        mockUserLoginDto = UserLoginDto.builder()
                .email("test@gmail.com")
                .password("password123")
                .build();

        mockUser = User.builder()
                .userUuid(UUID.randomUUID())
                .username("TestUser")
                .email("test@gmail.com")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("Should successfully save new user registered")
    void register_WhenDataIsValid_SavesUser() {
        when(userRepository.existsByEmail(mockUserDto.getEmail()))
                .thenReturn(Mono.just(false));
        when(userMapper.toEntity(mockUserDto))
                .thenReturn(mockUser);
        when(userRepository.save(mockUser))
                .thenReturn(Mono.just(mockUser));

        StepVerifier.create(userService.register(mockUserDto))
                .verifyComplete();

        verify(userRepository).existsByEmail(mockUserDto.getEmail());
        verify(userMapper).toEntity(mockUserDto);
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("Should throw UserExistsException when email already taken")
    void register_WhenEmailExists_ThrowsUserExistsException() {
        when(userRepository.existsByEmail(mockUserDto.getEmail()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userService.register(mockUserDto))
                        .expectErrorMatches(throwable -> throwable instanceof UserExistsException
                                && throwable.getMessage().equals("User with email " + mockUserDto.getEmail() + " already exists"))
                                .verify();

        verify(userRepository).existsByEmail(mockUserDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ValidationException when passwords do not match")
    void register_WhenPasswordsMismatch_ThrowsValidationException() {
        mockUserDto.setConfirmPassword("wrong_password");

        StepVerifier.create(userService.register(mockUserDto))
                .expectErrorMatches(throwable -> throwable instanceof ValidationException
                                                 && throwable.getMessage().equals("Passwords don't match"))
                .verify();

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw AuthenticationUserException when email is not found")
    void login_WhenEmailNotFound_ThrowsException() {
        when(userRepository.findByEmail(mockUserLoginDto.getEmail()))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.login(mockUserLoginDto))
                .expectErrorMatches(throwable -> throwable instanceof AuthenticationUserException
                                                 && throwable.getMessage().equals("Invalid email or password"))
                .verify();

        verify(userRepository).findByEmail(mockUserLoginDto.getEmail());
    }

    @Test
    @DisplayName("Should return user when email exists")
    void login_WhenCredentialsCorrect_ReturnsUser() {
        when(userRepository.findByEmail(mockUserLoginDto.getEmail()))
                .thenReturn(Mono.just(mockUser));

        StepVerifier.create(userService.login(mockUserLoginDto))
                .expectNextMatches(user -> user.getEmail().equals(mockUser.getEmail()))
                .verifyComplete();

        verify(userRepository).findByEmail(mockUserLoginDto.getEmail());
    }

    @Test
    @DisplayName("Should throw AuthenticationUserException when passwords do not match")
    void login_WhenPasswordIncorrect_ThrowsAuthenticationUserException() {
        User testUser = User.builder()
                .email(mockUserLoginDto.getEmail())
                .password("wrong_password")
                .build();

        when(userRepository.findByEmail(mockUserLoginDto.getEmail()))
                .thenReturn(Mono.just(testUser));

        StepVerifier.create(userService.login(mockUserLoginDto))
                .expectErrorMatches(throwable -> throwable instanceof AuthenticationUserException
                                                 && throwable.getMessage().equals("Invalid email or password"))
                .verify();

        verify(userRepository).findByEmail(mockUserLoginDto.getEmail());
    }
}