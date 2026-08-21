package com.example.ecommerce.service;

import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.exception.UnathorizedException;
import com.example.ecommerce.model.SupportMessage;
import com.example.ecommerce.model.SupportTicket;
import com.example.ecommerce.model.SupportTicketStatus;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.SupportTicketRepository;
import com.example.ecommerce.request.CreateTicketRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;

@Service
public class SupportTicketService {
    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketService(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    public SupportTicket createTicket(User user, CreateTicketRequest request) {
        SupportTicket ticket = new SupportTicket();
        ticket.setSubject(request.getSubject());
        ticket.setUser(user);
        ticket.setStatus(SupportTicketStatus.OPEN);
        ticket.setCreatedAt(Instant.now());
        ticket.setMessages(new ArrayList<>());

        SupportMessage message = new SupportMessage();
        message.setSender(user);
        message.setMessage(request.getMessage());
        message.setCreatedAt(Instant.now());
        message.setTicket(ticket);
        ticket.getMessages().add(message);

        return supportTicketRepository.save(ticket);
    }

    public Page<SupportTicket> findMyTickets(User user, Pageable pageable) {
        return supportTicketRepository.findByUserId(user.getId(), pageable);
    }

    public Page<SupportTicket> findAll(Pageable pageable) {
        return supportTicketRepository.findAll(pageable);
    }

    public SupportTicket findById(User user, Long ticketId) {
        SupportTicket ticket = getTicket(ticketId);
        assertAccess(user, ticket);
        return ticket;
    }

    public SupportTicket addMessage(User user, Long ticketId, String message) {
        SupportTicket ticket = getTicket(ticketId);
        assertAccess(user, ticket);

        SupportMessage newMessage = new SupportMessage();
        newMessage.setSender(user);
        newMessage.setMessage(message);
        newMessage.setCreatedAt(Instant.now());
        newMessage.setTicket(ticket);
        ticket.getMessages().add(newMessage);

        return supportTicketRepository.save(ticket);
    }

    public SupportTicket closeTicket(Long ticketId) {
        SupportTicket ticket = getTicket(ticketId);
        ticket.setStatus(SupportTicketStatus.CLOSED);
        return supportTicketRepository.save(ticket);
    }

    private SupportTicket getTicket(Long ticketId) {
        return supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Jegy nem található."));
    }

    private void assertAccess(User user, SupportTicket ticket) {
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));
        if (!isAdmin && !ticket.getUser().getId().equals(user.getId())) {
            throw new UnathorizedException("Ez a jegy nem a bejelentkezett felhasználóhoz tartozik.");
        }
    }
}
