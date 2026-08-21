package com.example.ecommerce.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank(message = "A szállítási cím megadása kötelező.")
    private String address;
    @NotNull(message = "A szállítási mód megadása kötelező.")
    private Long shippingMethodId;
    @NotNull(message = "A fizetési mód megadása kötelező.")
    private Long paymentMethodId;
    private String couponCode;
}
