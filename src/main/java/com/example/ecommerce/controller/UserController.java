package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.request.SigninRequest;
import com.example.ecommerce.request.SignupRequest;
import com.example.ecommerce.security.jwt.JwtTokenResponse;
import com.example.ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtTokenResponse> signin(@Valid @RequestBody SigninRequest loginRequest) {
        JwtTokenResponse jwtTokenResponse = userService.signin(loginRequest);
        return ResponseEntity.ok(jwtTokenResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@Valid @RequestBody SignupRequest signupRequest) {
        User user = userService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/loggedUser")
    public ResponseEntity<User> getLoggedUser() {
        User user = userService.getAuthenticatedUser();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(user);
    }

    @GetMapping("/findAll")
    public ResponseEntity<Page<User>> findAll(Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/findById/:id")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/logout")
    public void logout(HttpServletResponse response) {
        userService.logout(response);
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        //userService.resetPassword(token, newPassword);
    }

}
