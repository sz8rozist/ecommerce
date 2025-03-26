package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")  // A megfelelő idegen kulcs
    private Product product;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")  // A megfelelő idegen kulcs
    private Order order;
}
