package com.example.ecommerce.exception;

import com.example.ecommerce.response.EcommerceAPIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<EcommerceAPIResponse> handleInvalidArgumentException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().stream().map(error-> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new EcommerceAPIResponse(errors));
    }
    @ExceptionHandler(EcommerceApplicationException.class)
    public ResponseEntity<EcommerceAPIResponse> handleApplicationException(EcommerceApplicationException exception) {
        return new ResponseEntity<>(new EcommerceAPIResponse(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<EcommerceAPIResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        return new ResponseEntity<>(new EcommerceAPIResponse(exception.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<EcommerceAPIResponse> handleInvalidArgumentException(InvalidCredentialsException exception) {
        return ResponseEntity.badRequest().body(new EcommerceAPIResponse(exception.getMessage()));
    }
}
