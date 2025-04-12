package com.example.ecommerce.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = PasswordMatchValidator.class)
public @interface PasswordMatch {
    String message() default "A két jelszó nem egyezik!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}