package com.example.ecommerce.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NonNull
public class JwtTokenResponse {
    private String token;
}
