package com.example.ecommerce.controller;

import com.example.ecommerce.model.PaymentMethod;
import com.example.ecommerce.service.PaymentMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment_method")
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentMethod>> findAllPaymentMethods() {
        return ResponseEntity.ok(paymentMethodService.findAll());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<PaymentMethod> createPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMethodService.create(paymentMethod));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.delete(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    public ResponseEntity<PaymentMethod> updatePaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        return ResponseEntity.ok(paymentMethodService.update(paymentMethod));
    }
}
