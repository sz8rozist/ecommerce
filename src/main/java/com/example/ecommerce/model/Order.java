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

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Új mező: rendeléshez tartozó felhasználó
    private User user;

    @ManyToOne
    @JoinColumn(name = "shipping_method_id")
    private ShippingMethod shippingMethod;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;
}
