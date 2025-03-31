package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.request.SigninRequest;
import com.example.ecommerce.request.SignupRequest;
import com.example.ecommerce.response.AuthResponse;
import com.example.ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signin")
    public User signin(@RequestBody @Valid SigninRequest loginRequest, HttpServletResponse response) {
        return userService.signin(loginRequest, response);
    }

    @PostMapping("/signup")
    public User signup(@RequestBody @Valid SignupRequest signupRequest) {
        return userService.signup(signupRequest);
    }

    @GetMapping("/loggedUser")
    public User getLoggedUser() {
        return userService.getAuthenticatedUser();
    }

    @GetMapping("/findAll")
    public Page<User> findAll(Pageable pageable) {
        return userService.findAll(pageable);
    }

    @GetMapping("/findById/:id")
    public User findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/logout")
    public void logout(HttpServletResponse response) {
        userService.logout(response);
    }
}
