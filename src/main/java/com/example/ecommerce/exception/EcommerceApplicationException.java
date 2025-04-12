package com.example.ecommerce.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EcommerceApplicationException extends RuntimeException{
    private String field;

    public EcommerceApplicationException(String msg, String field){
        super(msg);
        this.field = field;
    }

    public EcommerceApplicationException(String msg){
        super(msg);
    }
}
