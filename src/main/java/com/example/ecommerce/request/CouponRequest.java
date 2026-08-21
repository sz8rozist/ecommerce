package com.example.ecommerce.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CouponRequest {
    @NotBlank(message = "A kuponkód megadása kötelező.")
    private String code;
    @Min(value = 1, message = "A kedvezmény legalább 1% kell legyen.")
    @Max(value = 99, message = "A kedvezmény legfeljebb 99% lehet.")
    private int percentage;
    @NotNull(message = "A kezdő dátum megadása kötelező.")
    private LocalDate startDate;
    @NotNull(message = "A végdátum megadása kötelező.")
    private LocalDate endDate;
}
