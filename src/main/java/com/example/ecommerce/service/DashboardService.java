package com.example.ecommerce.service;

import com.example.ecommerce.model.SupportTicketStatus;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.SupportTicketRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.response.DashboardStatsDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SupportTicketRepository supportTicketRepository;

    public DashboardService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                             ProductRepository productRepository, UserRepository userRepository,
                             SupportTicketRepository supportTicketRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.supportTicketRepository = supportTicketRepository;
    }

    public DashboardStatsDTO getStats() {
        Double revenue = orderItemRepository.sumRevenue();
        return new DashboardStatsDTO(
                orderRepository.count(),
                revenue != null ? revenue : 0,
                productRepository.count(),
                userRepository.count(),
                supportTicketRepository.countByStatus(SupportTicketStatus.OPEN),
                orderItemRepository.findTopSellingProducts(PageRequest.of(0, 5))
        );
    }
}
