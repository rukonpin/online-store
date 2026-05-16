package com.online.store.controller.user.web;

import com.online.store.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserViewController {

    private final UserService userService;

    @GetMapping("/register")
    public String register() {

        return "register";
    }

    @GetMapping("/login")
    public String login() {

        return "login";
    }
}
