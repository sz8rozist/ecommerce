package com.example.ecommerce.controller;

import com.example.ecommerce.model.ShippingMethod;
import com.example.ecommerce.service.ShippingMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<List<ShippingMethod>> findAllShippingMethods() {
        return ResponseEntity.ok(shippingMethodService.findAll());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ShippingMethod> createShippingMethod(@RequestBody ShippingMethod shippingMethod) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shippingMethodService.create(shippingMethod));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShippingMethod(@PathVariable Long id) {
        shippingMethodService.delete(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    public ResponseEntity<ShippingMethod> updateShippingMethod(@RequestBody ShippingMethod shippingMethod) {
        return ResponseEntity.ok(shippingMethodService.update(shippingMethod));
    }
}
