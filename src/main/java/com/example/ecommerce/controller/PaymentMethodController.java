package com.example.ecommerce.controller;

import com.example.ecommerce.model.PaymentMethod;
import com.example.ecommerce.service.PaymentMethodService;
import org.hibernate.query.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<PaymentMethod>> findAll() {
        return ResponseEntity.ok(paymentMethodService.findAll());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        paymentMethodService.delete(id);
    }

    @PutMapping
    public ResponseEntity<PaymentMethod> update(@RequestBody PaymentMethod paymentMethod) {
        return ResponseEntity.ok(paymentMethodService.update(paymentMethod));
    }
}
