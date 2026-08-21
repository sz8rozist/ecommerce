package com.example.ecommerce.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTicketRequest {
    @NotBlank(message = "A tárgy megadása kötelező.")
    private String subject;
    @NotBlank(message = "Az üzenet megadása kötelező.")
    private String message;
}
