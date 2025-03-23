package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.request.SigninRequest;
import com.example.ecommerce.request.SignupRequest;
import com.example.ecommerce.response.AuthResponse;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public AuthResponse signin(@RequestBody @Valid SigninRequest loginRequest) {
        return userService.signin(loginRequest);
    }

    @PostMapping("/signup")
    public User signup(@RequestBody @Valid SignupRequest signupRequest) {
        return userService.signup(signupRequest);
    }

    @GetMapping("/loggedUser")
    public User getLoggedUser() {
        return userService.getAuthenticatedUser();
    }
}
