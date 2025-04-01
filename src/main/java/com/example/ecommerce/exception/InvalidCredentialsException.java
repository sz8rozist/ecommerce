package com.example.ecommerce.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvalidCredentialsException extends RuntimeException {
    private String field;

    public InvalidCredentialsException(String message, String field) {
        super(message);
        this.field = field;
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
