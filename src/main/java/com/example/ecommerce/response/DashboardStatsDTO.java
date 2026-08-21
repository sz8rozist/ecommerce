package com.example.ecommerce.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private long totalOrders;
    private double totalRevenue;
    private long totalProducts;
    private long totalUsers;
    private long openSupportTickets;
    private List<TopProductDTO> topProducts;
}
