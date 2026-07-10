package com.online.store.controller.user.api;

import com.online.store.dto.user.UserLoginDto;
import com.online.store.dto.user.UserRegistrationDto;
import com.online.store.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserRestController {

    private final UserService userService;

    @PostMapping("/register")
    public Mono<ResponseEntity<Void>> register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {

        return userService.register(userRegistrationDto)
                        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Void>> login(@Valid @RequestBody UserLoginDto userLoginDto, WebSession webSession) {

        return userService.login(userLoginDto)
                .flatMap(user -> {
                    webSession.getAttributes().put("user_id", user.getUserUuid());
                    webSession.getAttributes().put("username", user.getUsername());
                    webSession.setMaxIdleTime(Duration.ofHours(1));

                    return Mono.just(ResponseEntity.ok().build());
                });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(WebSession webSession) {

        return webSession.invalidate()
                .thenReturn(ResponseEntity.ok().build());
    }
}
