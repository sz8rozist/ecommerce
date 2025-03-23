package com.example.ecommerce.validators;

import jakarta.validation.Constraint;
import org.springframework.messaging.handler.annotation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    String message() default "A felhasználónév már foglalt!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
