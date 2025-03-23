package com.example.ecommerce.validators;

import jakarta.validation.Constraint;
import org.springframework.messaging.handler.annotation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    String message() default "Az e-mail cím már foglalt!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}