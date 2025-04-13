package com.example.ecommerce.repository;

import com.example.ecommerce.model.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
}
