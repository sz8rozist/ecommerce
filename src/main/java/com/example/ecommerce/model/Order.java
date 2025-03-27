package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant orderDate;

    private String orderAddress;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "orders_item", joinColumns = @JoinColumn(name = "orders_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "cart_id", referencedColumnName = "id"))
    private List<Cart> carts;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Új mező: rendeléshez tartozó felhasználó
    private User user;
}
