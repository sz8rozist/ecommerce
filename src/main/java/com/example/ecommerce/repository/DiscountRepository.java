package com.example.ecommerce.repository;

import com.example.ecommerce.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Query("SELECT d FROM Discount d WHERE d.product.id = :productId AND :today BETWEEN d.startDate AND d.endDate")
    Optional<Discount> findActiveByProductId(@Param("productId") Long productId, @Param("today") LocalDate today);

    @Query("SELECT d FROM Discount d WHERE :today BETWEEN d.startDate AND d.endDate")
    List<Discount> findAllActive(@Param("today") LocalDate today);

    void deleteByProductId(Long productId);
}
