package com.example.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A fizetési mód típusa kötelező.")
    @Enumerated(EnumType.STRING)
    private PaymentMethodType type;

    @NotBlank(message = "A megjelenített név kötelező.")
    private String displayName;

    private boolean active;

    @PositiveOrZero(message = "A tranzakciós díj nem lehet negatív.")
    private int transactionFee;
}
