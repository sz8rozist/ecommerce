package com.example.ecommerce.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ApiResponseHandler implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // Ha már EcommerceAPIResponse, ne csomagoljuk újra
        if (body instanceof EcommerceAPIResponse) {
            return body;
        }

        // Ha a válasz null, akkor egy alapértelmezett üzenetet adunk vissza
        if (body == null) {
            return new EcommerceAPIResponse("Nincs adat a válaszban.");
        }

        // Ha a válasz típusa String, akkor JSON-be kell csomagolni, különben String maradna
        if (body instanceof String) {
            return new EcommerceAPIResponse(body);
        }

        // Ha a válasz bináris adat (pl. fájl letöltés), akkor ne csomagoljuk JSON-be
        if (body instanceof byte[]) {
            return body;
        }

        // Alapértelmezett csomagolás minden egyéb válaszra
        return new EcommerceAPIResponse(body);
    }
}
