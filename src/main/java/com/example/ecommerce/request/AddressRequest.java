package com.example.ecommerce.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank(message = "A cím elnevezése kötelező.")
    private String label;

    @NotBlank(message = "A név megadása kötelező.")
    private String fullName;

    private String phone;

    @NotBlank(message = "A cím megadása kötelező.")
    private String addressLine;

    private boolean isDefault;
}
