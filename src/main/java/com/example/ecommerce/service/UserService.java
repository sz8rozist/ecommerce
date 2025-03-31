package com.example.ecommerce.service;

import com.example.ecommerce.exception.EcommerceApplicationException;
import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.exception.InvalidCredentialsException;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.request.SigninRequest;
import com.example.ecommerce.request.SignupRequest;
import com.example.ecommerce.response.AuthResponse;
import com.example.ecommerce.security.jwt.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

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
        User user = userRepository.findByUsername(username);
        if (user == null) throw new EcommerceApplicationException("Nincs bejelentkezve felhasználó!");
        return user;
    }

    public User signin(SigninRequest loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken(authentication);
            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setHttpOnly(true);  // Csak szerver férhet hozzá
            cookie.setSecure(true); // HTTPS esetén kell
            cookie.setPath("/"); // Az egész alkalmazásra érvényes
            // Ha csak sessionig akarod tárolni, akkor ezt NE állítsd be:
            // cookie.setMaxAge(60 * 60 * 24); // Ha akarod, itt állíthatod be a cookie érvényességi idejét

            response.addCookie(cookie);
            return userRepository.findByUsername(loginRequest.getUsername());
        } catch (AuthenticationException authenticationException) {
            throw new InvalidCredentialsException("Hibás felhasználónév vagy jelszó!");
        }
    }


    public User signup(SignupRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        List<Role> roles = new ArrayList<>();
        roleRepository.findByName(request.getRole()).ifPresent(roles::add);
        if (roles.isEmpty()) {
            throw new EcommerceApplicationException("Nem található jogosultság!");
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Nem található felhasználó."));
    }

    public void logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
