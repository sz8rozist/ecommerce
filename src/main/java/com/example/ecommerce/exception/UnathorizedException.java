package com.example.ecommerce.exception;

public class UnathorizedException extends RuntimeException {
    public UnathorizedException(String msg) {
        super(msg);
    }
}
