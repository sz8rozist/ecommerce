package com.example.ecommerce.repository;

import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.response.TopProductDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByProductId(Long productId);

    @Query("SELECT SUM(oi.unitPrice * oi.quantity) FROM OrderItem oi")
    Double sumRevenue();

    @Query("SELECT oi.product.name AS name, SUM(oi.quantity) AS totalQuantity FROM OrderItem oi " +
            "GROUP BY oi.product.id, oi.product.name ORDER BY SUM(oi.quantity) DESC")
    List<TopProductDTO> findTopSellingProducts(Pageable pageable);
}
