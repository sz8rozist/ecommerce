package com.example.ecommerce.service;

import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.model.PaymentMethod;
import com.example.ecommerce.repository.PaymetnMethodRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {
    private final PaymetnMethodRepository paymetnMethodRepository;

    public PaymentMethodService(PaymetnMethodRepository paymetnMethodRepository) {
        this.paymetnMethodRepository = paymetnMethodRepository;
    }

    public List<PaymentMethod> findAll() {
        return paymetnMethodRepository.findAll();
    }

    public PaymentMethod create(PaymentMethod paymentMethod) {
        paymentMethod.setId(null);
        return paymetnMethodRepository.save(paymentMethod);
    }

    public void delete(Long id) {
        PaymentMethod paymentMethod = paymetnMethodRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Nem található fizetési mód!"));
        paymetnMethodRepository.delete(paymentMethod);
    }

    public PaymentMethod update(PaymentMethod paymentMethod) {
        PaymentMethod existing = paymetnMethodRepository.findById(paymentMethod.getId())
                .orElseThrow(() -> new EntityNotFoundException("Nem található fizetési mód!"));

        existing.setDisplayName(paymentMethod.getDisplayName());
        existing.setActive(paymentMethod.isActive());
        existing.setTransactionFee(paymentMethod.getTransactionFee());
        existing.setType(paymentMethod.getType());

        return paymetnMethodRepository.save(existing);
    }
}
