package com.example.ecommerce.service;

import com.example.ecommerce.exception.EcommerceApplicationException;
import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.exception.InvalidCredentialsException;
import com.example.ecommerce.exception.UnathorizedException;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.request.SigninRequest;
import com.example.ecommerce.request.SignupRequest;
import com.example.ecommerce.security.jwt.JwtTokenResponse;
import com.example.ecommerce.security.jwt.JwtUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JavaMailSender mailSender;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, RoleRepository roleRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.mailSender = mailSender;
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnathorizedException("Nincs bejelentkezve felhasználó!");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UnathorizedException("A felhasználó nem található az adatbázisban!");
        }

        return user;
    }

    public JwtTokenResponse signin(SigninRequest loginRequest) {
        try {
            User user = userRepository.findByUsername(loginRequest.getUsername());
            if (user == null) {
                throw new InvalidCredentialsException("Hibás felhasználónév!", "username");
            }
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new InvalidCredentialsException("Hibás jelszó!", "password");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // JWT token generálása és beállítása cookie-ként
            String jwt = jwtUtils.generateToken(authentication);
            return new JwtTokenResponse(jwt);
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Hibás belépési adatok!"); // Általános hibaüzenet
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

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("Nem található felhasználó ezzel az e-mail címmel.");
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        sendResetEmail(user.getEmail(), token);
    }

    private void sendResetEmail(String email, String token) {
        String resetUrl = "http://localhost:8080/user/reset-password?token=" + token;
        String subject = "Jelszó-visszaállítás";
        String body = "Kérlek kattints az alábbi linkre a jelszavad visszaállításához: <a href=\"" + resetUrl + "\">Visszaállítás</a>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EcommerceApplicationException("");
        }
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user == null) {
            throw new EntityNotFoundException("Érvénytelen token.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

}
