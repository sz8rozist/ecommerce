package com.example.ecommerce.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Csak a nem null értékeket tartalmazza
public class EcommerceAPIResponse {
    private Map<String, String> errors = new HashMap<>();
    private Timestamp timestamp;
    private Object data;
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
    public Map<String, String> getErrors() {
        return errors.isEmpty() ? null : errors;
    }
}
