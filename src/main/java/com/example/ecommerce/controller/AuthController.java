package com.example.ecommerce.controller;

import com.example.ecommerce.exception.EcommerceApplicationException;
import com.example.ecommerce.model.User;
import com.example.ecommerce.request.LoginRequest;
import com.example.ecommerce.response.AuthResponse;
import com.example.ecommerce.response.EcommerceAPIResponse;
import com.example.ecommerce.service.user.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("${api.prefix}/auth")
@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<EcommerceAPIResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(new EcommerceAPIResponse(response));
    }

    @GetMapping
    public ResponseEntity<EcommerceAPIResponse> getUserInfo() {
        return ResponseEntity.ok(new EcommerceAPIResponse("haha"));
    }
}
