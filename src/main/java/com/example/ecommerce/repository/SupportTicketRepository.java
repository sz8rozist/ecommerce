package com.example.ecommerce.repository;

import com.example.ecommerce.model.SupportTicket;
import com.example.ecommerce.model.SupportTicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    Page<SupportTicket> findByUserId(Long userId, Pageable pageable);

    long countByStatus(SupportTicketStatus status);
}
