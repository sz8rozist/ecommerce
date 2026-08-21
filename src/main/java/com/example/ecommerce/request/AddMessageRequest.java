package com.example.ecommerce.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddMessageRequest {
    @NotBlank(message = "Az üzenet megadása kötelező.")
    private String message;
}
