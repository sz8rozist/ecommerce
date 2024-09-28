package com.example.ecommerce.service.user;

import com.example.ecommerce.exception.InvalidCredentialsException;
import com.example.ecommerce.request.LoginRequest;
import com.example.ecommerce.response.*;
import com.example.ecommerce.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    public AuthResponse login(LoginRequest loginRequest) {
        try{
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken(authentication);
            return new AuthResponse(jwt);
        }catch (AuthenticationException authenticationException){
            throw new InvalidCredentialsException("Hibás felhasználónév vagy jelszó!");
        }
    }

}
