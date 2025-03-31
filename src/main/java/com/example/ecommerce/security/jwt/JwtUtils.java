package com.example.ecommerce.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.ecommerce.security.user.EcommerceUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    @Value("${auth.token.expirationInMils}")
    private int expirationTime;

    public String generateToken(Authentication authentication) {
        EcommerceUserDetails userDetails = (EcommerceUserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Az Auth0 JWT könyvtár használatával történő token generálás
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret); // Az aláírási algoritmus

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("id", userDetails.getId())
                .withClaim("roles", roles)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime)) // lejárat
                .sign(algorithm); // Aláírás
    }

    public String getUsernameFromToken(String token) {
        // Token dekódolása és a felhasználónév visszaadása
        return JWT.decode(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            // Az Auth0 JWT könyvtár nem rendelkezik beépített validálással, ezért manuálisan kell validálni
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWT.require(algorithm)
                    .build()
                    .verify(token); // A token érvényesítése

            return true;
        } catch (Exception e) {
            throw new JwtException("Invalid JWT token: " + e.getMessage());
        }
    }
}
