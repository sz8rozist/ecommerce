package com.example.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class ShippingMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A szállítási mód típusa kötelező.")
    @Enumerated(EnumType.STRING)
    private ShippingMethodType type;

    @NotBlank(message = "A megjelenített név kötelező.")
    private String displayName;

    private boolean active;

    @PositiveOrZero(message = "Az ár nem lehet negatív.")
    private double price;
}
