package com.example.ecommerce.request;

import com.example.ecommerce.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilter {
    private OrderStatus status;
    private String username;
    private LocalDate fromDate;
    private LocalDate toDate;
}
