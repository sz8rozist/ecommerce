package com.example.ecommerce.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


@Data
public class EcommerceAPIResponse {
    private Map<String, String> errors = new HashMap<>();
    private Object data;
    private Timestamp timestamp;
    private String message;

    public EcommerceAPIResponse(Object data) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.data = data;
    }
    public EcommerceAPIResponse(Map<String, String> errors) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.errors = errors;
    }
    public EcommerceAPIResponse(String message){
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.message = message;
    }
}
