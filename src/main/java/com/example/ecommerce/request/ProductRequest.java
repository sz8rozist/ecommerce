package com.example.ecommerce.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank(message = "A termék nevének megadása kötelező.")
    private String name;
    @NotNull(message = "A termék árának megadása kötelező.")
    @Min(value = 1, message = "A termék árának legalább 1-nek kell lennie.")
    private Integer price;
    @NotBlank(message = "A termék leírásának megadása kötelező.")
    private String description;
    private Long categoryId;
    private Integer stockQuantity;
    private Integer minStockThreshold;
}
