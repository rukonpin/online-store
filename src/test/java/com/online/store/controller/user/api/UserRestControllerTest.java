package com.online.store.controller.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.store.dto.user.UserLoginDto;
import com.online.store.dto.user.UserRegistrationDto;
import com.online.store.exception.user.AuthenticationUserException;
import com.online.store.exception.user.UserExistsException;
import com.online.store.exception.ValidationException;
import com.online.store.model.user.User;
import com.online.store.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private UserRegistrationDto testRegistrationDto;
    private UserLoginDto testLoginDto;
    private User testUser;

    @Autowired
    private ObjectMapper objectMapper;

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
                .uuid(UUID.randomUUID())
                .username("Test")
                .password("password123")
                .email("test@gmail.com")
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register - should return create status")
    void register_WithRequestBody_ReturnCreatedStatus() throws Exception {

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegistrationDto)))
                //.andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/auth/register - should throw UserExistsException when user already exist")
    void register_WithAlreadyExistUser_ThrowUserExistsException() throws Exception {

        String email = testRegistrationDto.getEmail();

        doThrow(new UserExistsException(email))
                .when(userService).register(eq(testRegistrationDto));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegistrationDto)))
                //.andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("User with email " + email + " already exists"));
    }

    @Test
    @DisplayName("POST /api/auth/register - should throw ValidationException when passwords do not match")
    void register_WithInvalidPassword_ThrowValidationException() throws Exception {

        doThrow(new ValidationException("Passwords don't match"))
                .when(userService).register(eq(testRegistrationDto));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegistrationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Passwords don't match"));
    }

    @Test
    @DisplayName("POST /api/auth/register - should throw ValidationException when incorrect email")
    void register_WithInvalidEmail_ThrowValidationException() throws Exception {

        testRegistrationDto.setEmail("test.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegistrationDto)))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Неверный формат email"));
    }

    @Test
    @DisplayName("POST /api/auth/login - should return presence of attribute in session")
    void login_WithExistsUser_ReturnsSession() throws Exception {

        when(userService.login(testLoginDto))
            .thenReturn(testUser);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginDto)))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(request().sessionAttribute("user_id", testUser.getUuid()));
    }

    @Test
    @DisplayName("POST /api/auth/login - should throw AuthenticationUserException when passwords do not match")
    void login_WithPasswordsMismatch_ThrowsAuthenticationUserException() throws Exception {

        when(userService.login(testLoginDto))
                .thenThrow(new AuthenticationUserException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginDto)))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}