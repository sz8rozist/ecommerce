package com.example.ecommerce.service;

import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.model.ShippingMethod;
import com.example.ecommerce.repository.ShippingMethodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingMethodService {
    private final ShippingMethodRepository shippingMethodRepository;

    public ShippingMethodService(ShippingMethodRepository shippingMethodRepository) {
        this.shippingMethodRepository = shippingMethodRepository;
    }

    public List<ShippingMethod> findAll() {
        return shippingMethodRepository.findAll();
    }

    public void delete(Long id) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Nem található szállítási mód!"));
        shippingMethodRepository.delete(shippingMethod);
    }

    public ShippingMethod update(ShippingMethod shippingMethod) {
        ShippingMethod existing = shippingMethodRepository.findById(shippingMethod.getId())
                .orElseThrow(() -> new EntityNotFoundException("Nem található szállítási mód!"));

        existing.setDisplayName(shippingMethod.getDisplayName());
        existing.setActive(shippingMethod.isActive());
        existing.setPrice(shippingMethod.getPrice());
        existing.setType(shippingMethod.getType());

        return shippingMethodRepository.save(existing);
    }
}
