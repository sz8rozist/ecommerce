package com.example.ecommerce.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "A felhasználónév megadása kötelező.")
    private String username;
    @NotBlank(message = "A jelszó megadása kötelező.")
    private String password;
}
