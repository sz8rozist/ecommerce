package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.request.ResetPasswordRequest;
import com.example.ecommerce.request.SigninRequest;
import com.example.ecommerce.request.SignupRequest;
import com.example.ecommerce.request.UserFilter;
import com.example.ecommerce.security.jwt.JwtTokenResponse;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/findAll")
    public ResponseEntity<Page<User>> findAll(@RequestParam(required = false, defaultValue = "0") int page,
                                              @RequestParam(required = false, defaultValue = "10") int size,
                                              @RequestParam(required = false) String username,
                                              @RequestParam(required = false, defaultValue = "id") String sortBy,
                                              @RequestParam(required = false, defaultValue = "asc") String sortOrder
                                             ) {
        Sort.Direction direction = "asc".equals(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<User> users = userService.findAll(pageable, new UserFilter(username));
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/findById/:id")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/logout")
    public void logout() {
        userService.logout();
    }

    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        userService.resetPassword(resetPasswordRequest);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/admin-role")
    public ResponseEntity<User> setAdminRole(@PathVariable Long id, @RequestParam boolean grant) {
        return ResponseEntity.ok(userService.setAdminRole(id, grant));
    }

}
