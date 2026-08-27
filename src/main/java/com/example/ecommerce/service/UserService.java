package com.example.ecommerce.service;

import com.example.ecommerce.exception.EcommerceApplicationException;
import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.exception.InvalidCredentialsException;
import com.example.ecommerce.exception.UnathorizedException;
import com.example.ecommerce.model.Role;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.RoleRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.request.ResetPasswordRequest;
import com.example.ecommerce.request.SigninRequest;
import com.example.ecommerce.request.SignupRequest;
import com.example.ecommerce.request.UpdateProfileRequest;
import com.example.ecommerce.request.UserFilter;
import com.example.ecommerce.security.jwt.JwtTokenResponse;
import com.example.ecommerce.security.jwt.JwtUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
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

import java.util.Collection;
import java.util.HashSet;
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
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new EcommerceApplicationException("Az alapértelmezett felhasználói jogosultság nem található!"));
        user.setRoles(List.of(userRole));
        return userRepository.save(user);
    }

    public Page<User> findAll(Pageable pageable, UserFilter filter) {
        boolean hasUsername = filter != null && filter.getUsername() != null && !filter.getUsername().isEmpty();
        boolean hasRole = filter != null && filter.getRole() != null && !filter.getRole().isEmpty();
        if (!hasUsername && !hasRole) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findAll(filterPredicate(filter), pageable);
    }

    public Specification<User> filterPredicate(UserFilter filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (filter.getUsername() != null && !filter.getUsername().isEmpty()) {
                // Szűrés a felhasználónév alapján
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("username"), "%" + filter.getUsername() + "%"));
            }

            if (filter.getRole() != null && !filter.getRole().isEmpty()) {
                // Szűrés a szerepkör alapján
                query.distinct(true);
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.join("roles").get("name"), filter.getRole()));
            }

            return predicate;
        };
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Nem található felhasználó."));
    }

    public void logout() {
        SecurityContextHolder.clearContext();
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
        String resetUrl = "http://localhost:4200/reset-password/" + token;
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
            throw new EcommerceApplicationException("Hiba történt az email küldés közben!");
        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken());
        if (user == null) {
            throw new EntityNotFoundException("Érvénytelen token.");
        }
        boolean isOldPasswordValid = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        if (!isOldPasswordValid) {
            throw new EcommerceApplicationException("A régi jelszó helytelen.", "oldPassword");
        }

        if(!request.getNewPassword().equals(request.getNewPasswordConfirm())){
            throw new EcommerceApplicationException("A két jelszó nem egyezik!", "newPassword");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->  new EntityNotFoundException("Nem található felhasználó"));
        userRepository.delete(user);
    }

    public User setAdminRole(Long id, boolean grantAdmin) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Nem található felhasználó"));
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new EcommerceApplicationException("Az ADMIN jogosultság nem található!"));

        Collection<Role> roles = new HashSet<>(user.getRoles());
        if (grantAdmin) {
            roles.add(adminRole);
        } else {
            roles.removeIf(role -> "ADMIN".equals(role.getName()));
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User updateProfile(User user, UpdateProfileRequest request) {
        user.setAddress(request.getAddress());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        return userRepository.save(user);
    }
}
