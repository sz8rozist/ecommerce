package com.example.ecommerce.controller;

import com.example.ecommerce.model.SupportTicket;
import com.example.ecommerce.request.AddMessageRequest;
import com.example.ecommerce.request.CreateTicketRequest;
import com.example.ecommerce.service.SupportTicketService;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/support/tickets")
public class SupportTicketController {
    private final SupportTicketService supportTicketService;
    private final UserService userService;

    public SupportTicketController(SupportTicketService supportTicketService, UserService userService) {
        this.supportTicketService = supportTicketService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<SupportTicket> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.ok(supportTicketService.createTicket(userService.getAuthenticatedUser(), request));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<SupportTicket>> getMyTickets(@RequestParam(required = false, defaultValue = "0") int page,
                                                              @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(supportTicketService.findMyTickets(userService.getAuthenticatedUser(), pageable));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<SupportTicket>> getAllTickets(@RequestParam(required = false, defaultValue = "0") int page,
                                                                @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(supportTicketService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportTicket> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(supportTicketService.findById(userService.getAuthenticatedUser(), id));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<SupportTicket> addMessage(@PathVariable Long id, @Valid @RequestBody AddMessageRequest request) {
        return ResponseEntity.ok(supportTicketService.addMessage(userService.getAuthenticatedUser(), id, request.getMessage()));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/close")
    public ResponseEntity<SupportTicket> closeTicket(@PathVariable Long id) {
        return ResponseEntity.ok(supportTicketService.closeTicket(id));
    }
}
