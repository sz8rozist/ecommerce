package com.example.ecommerce.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ReviewRequest {
    @Min(value = 1, message = "Az értékelés legalább 1 kell legyen.")
    @Max(value = 5, message = "Az értékelés legfeljebb 5 lehet.")
    private int rating;
    private String comment;
}
