package com.example.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")  // A megfelelő idegen kulcs
    private Product product;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"roles"})
    private User user;
    private int quantity;
}
