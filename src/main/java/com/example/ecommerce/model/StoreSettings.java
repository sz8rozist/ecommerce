package com.example.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class StoreSettings {
    @Id
    private Long id;

    @NotBlank(message = "A bolt neve kötelező.")
    private String storeName;

    @Email(message = "Érvénytelen email cím formátum.")
    private String contactEmail;

    @NotBlank(message = "A pénznem megadása kötelező.")
    private String currency;
}
