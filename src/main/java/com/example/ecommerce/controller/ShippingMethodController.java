package com.example.ecommerce.controller;

import com.example.ecommerce.model.ShippingMethod;
import com.example.ecommerce.service.ShippingMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipping_method")
public class ShippingMethodController {
    private final ShippingMethodService shippingMethodService;

    public ShippingMethodController(ShippingMethodService shippingMethodService) {
        this.shippingMethodService = shippingMethodService;
    }
    @GetMapping
    public ResponseEntity<List<ShippingMethod>> findAll() {
        return ResponseEntity.ok(shippingMethodService.findAll());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        shippingMethodService.delete(id);
    }

    @PutMapping
    public ResponseEntity<ShippingMethod> update(@RequestBody ShippingMethod shippingMethod) {
        return ResponseEntity.ok(shippingMethodService.update(shippingMethod));
    }
}
