package com.example.ecommerce.service;

import com.example.ecommerce.exception.EcommerceApplicationException;
import com.example.ecommerce.exception.InvalidCredentialsException;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.request.SigninRequest;
import com.example.ecommerce.request.SignupRequest;
import com.example.ecommerce.response.AuthResponse;
import com.example.ecommerce.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    public AuthResponse signin(SigninRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken(authentication);
            return new AuthResponse(jwt);
        } catch (AuthenticationException authenticationException) {
            throw new InvalidCredentialsException("Hibás felhasználónév vagy jelszó!");
        }
    }


    public User signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new EcommerceApplicationException("A felhasználónév már foglalt!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EcommerceApplicationException("Az e-mail cím már foglalt!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        List<Role> roles = new ArrayList<>();
        roleRepository.findByName(request.getRole()).ifPresent(roles::add);
        if(roles.isEmpty()){
            throw new EcommerceApplicationException("Nem található jogosultság!");
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }
}
