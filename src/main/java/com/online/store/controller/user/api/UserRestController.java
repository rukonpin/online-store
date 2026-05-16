package com.online.store.controller.user.api;

import com.online.store.dto.user.UserLoginDto;
import com.online.store.dto.user.UserRegistrationDto;
import com.online.store.model.user.User;
import com.online.store.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserRestController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {

        userService.register(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto userLoginDto, HttpSession session) {

        User user = userService.login(userLoginDto);
        session.setAttribute("user_id", user.getUuid());
        session.setAttribute("username", user.getUsername());
        session.setMaxInactiveInterval(60 * 60);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().build();
    }
}
