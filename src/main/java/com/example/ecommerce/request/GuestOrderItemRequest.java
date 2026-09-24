package com.example.ecommerce.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GuestOrderItemRequest {
    @NotNull(message = "A termék azonosítója kötelező.")
    private Long productId;

    @Min(value = 1, message = "A mennyiségnek legalább 1-nek kell lennie.")
    private int quantity;
}
