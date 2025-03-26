package com.example.ecommerce.model;

public enum OrderStatus {
    PENDING("Folyamatban"), SHIPPED("Elküldve"), DELIVERED("Kiszállítva"), CANCELED("Törölve");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
