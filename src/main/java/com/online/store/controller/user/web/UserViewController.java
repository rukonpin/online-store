package com.online.store.controller.user.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/")
public class UserViewController {

    @GetMapping("/register")
    public Mono<String> register() {
        return Mono.just("register");
    }

    @GetMapping("/login")
    public Mono<String> login() {
        return Mono.just("login");
    }
}
