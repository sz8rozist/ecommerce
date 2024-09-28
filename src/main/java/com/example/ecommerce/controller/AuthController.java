package com.example.ecommerce.controller;

import com.example.ecommerce.request.LoginRequest;
import com.example.ecommerce.response.AuthResponse;
import com.example.ecommerce.response.EcommerceAPIResponse;
import com.example.ecommerce.service.user.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
