package com.example.ecommerce.exception;

import com.example.ecommerce.response.EcommerceAPIResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(org.springframework.web.bind.MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        EcommerceAPIResponse response = new EcommerceAPIResponse(errors);
        return ResponseEntity.badRequest().body(response);
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
    public ResponseEntity<EcommerceAPIResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex.getField() == null) {
            errors.put("hiba", ex.getMessage());
        }
        errors.put(ex.getField(), ex.getMessage());
        EcommerceAPIResponse response = new EcommerceAPIResponse(errors);
        return ResponseEntity.badRequest().body(response);
    }
}
