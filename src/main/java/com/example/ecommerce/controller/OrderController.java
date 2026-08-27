package com.example.ecommerce.controller;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.request.GuestOrderRequest;
import com.example.ecommerce.request.OrderFilter;
import com.example.ecommerce.request.OrderRequest;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody OrderRequest request) {
        Order order = orderService.placeOrder(userService.getAuthenticatedUser(), request);
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/guest-place")
    public ResponseEntity<Order> placeGuestOrder(@Valid @RequestBody GuestOrderRequest request) {
        Order order = orderService.placeGuestOrder(request);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<Order>> getMyOrders(@RequestParam(required = false, defaultValue = "0") int page,
                                                     @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        return ResponseEntity.ok(orderService.findMyOrders(userService.getAuthenticatedUser(), pageable));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<Order>> getAllOrders(@RequestParam(required = false, defaultValue = "0") int page,
                                                      @RequestParam(required = false, defaultValue = "10") int size,
                                                      @RequestParam(required = false) OrderStatus status,
                                                      @RequestParam(required = false) String username,
                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        return ResponseEntity.ok(orderService.findAll(pageable, new OrderFilter(status, username, fromDate, toDate)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(userService.getAuthenticatedUser(), id));
    }

    @PutMapping("/update-status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Order> updateStatus(@RequestParam Long orderId, @RequestParam OrderStatus status) {
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/tracking-number")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Order> updateTrackingNumber(@PathVariable Long id, @RequestParam String trackingNumber) {
        Order order = orderService.updateTrackingNumber(id, trackingNumber);
        return ResponseEntity.ok(order);
    }
}
