package com.example.ecommerce.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GuestOrderRequest {
    @NotEmpty(message = "A kosár nem lehet üres.")
    @Valid
    private List<GuestOrderItemRequest> items;

    @NotBlank(message = "A név megadása kötelező.")
    private String guestName;

    @NotBlank(message = "Az email cím megadása kötelező.")
    @Email(message = "Érvénytelen email cím formátum.")
    private String guestEmail;

    private String guestPhone;

    @NotBlank(message = "A szállítási cím megadása kötelező.")
    private String address;

    @NotNull(message = "A szállítási mód megadása kötelező.")
    private Long shippingMethodId;

    @NotNull(message = "A fizetési mód megadása kötelező.")
    private Long paymentMethodId;

    private String couponCode;
}
