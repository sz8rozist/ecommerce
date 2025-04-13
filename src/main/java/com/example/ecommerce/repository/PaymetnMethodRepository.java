package com.example.ecommerce.repository;

import com.example.ecommerce.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymetnMethodRepository extends JpaRepository<PaymentMethod, Long> {
}
