package com.online.store.controller.user.api;

import com.online.store.dto.user.UserLoginDto;
import com.online.store.dto.user.UserRegistrationDto;
import com.online.store.exception.GlobalExceptionHandler;
import com.online.store.exception.user.AuthenticationUserException;
import com.online.store.exception.user.UserExistsException;
import com.online.store.exception.ValidationException;
import com.online.store.model.user.User;
import com.online.store.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest({UserRestController.class, GlobalExceptionHandler.class})
class UserRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserService userService;

    private UserRegistrationDto testRegistrationDto;
    private UserLoginDto testLoginDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testRegistrationDto = UserRegistrationDto.builder()
                .username("Test")
                .password("password123")
                .confirmPassword("password123")
                .email("test@gmail.com")
                .build();

        testLoginDto = UserLoginDto.builder()
                .email("test@gmail.com")
                .password("password123")
                .build();

        testUser = User.builder()
                .userUuid(UUID.randomUUID())
                .username("Test")
                .password("password123")
                .email("test@gmail.com")
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register - should return create status")
    void register_WithRequestBody_ReturnCreatedStatus() throws Exception {
        when(userService.register(eq(testRegistrationDto)))
                .thenReturn(Mono.empty());

        webTestClient.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testRegistrationDto)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    @DisplayName("POST /api/auth/register - should throw UserExistsException when user already exist")
    void register_WithAlreadyExistUser_ThrowUserExistsException() throws Exception {
        String email = testRegistrationDto.getEmail();

        when(userService.register(eq(testRegistrationDto)))
                .thenReturn(Mono.error(new UserExistsException(email)));

        webTestClient.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testRegistrationDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("User with email " + email + " already exists");
    }

    @Test
    @DisplayName("POST /api/auth/register - should throw ValidationException when passwords do not match")
    void register_WithInvalidPassword_ThrowValidationException() throws Exception {
        when(userService.register(eq(testRegistrationDto)))
                .thenReturn(Mono.error(new ValidationException("Passwords don't match")));

        webTestClient.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testRegistrationDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Passwords don't match");
    }

    @Test
    @DisplayName("POST /api/auth/register - should throw ValidationException when incorrect email")
    void register_WithInvalidEmail_ThrowValidationException() throws Exception {
        testRegistrationDto.setEmail("test.com");

        webTestClient.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testRegistrationDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Неверный формат email");
    }

    @Test
    @DisplayName("POST /api/auth/login - should return presence of attribute in session")
    void login_WithExistsUser_ReturnsSession() throws Exception {
        when(userService.login(eq(testLoginDto)))
                .thenReturn(Mono.just(testUser));

        webTestClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testLoginDto)
                .exchange()
                .expectStatus().isOk()
                .expectCookie().exists("SESSION");
    }

    @Test
    @DisplayName("POST /api/auth/login - should throw AuthenticationUserException when passwords do not match")
    void login_WithPasswordsMismatch_ThrowsAuthenticationUserException() throws Exception {
        when(userService.login(eq(testLoginDto)))
                .thenReturn(Mono.error(new AuthenticationUserException("Invalid email or password")));

        webTestClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testLoginDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid email or password");
    }
}