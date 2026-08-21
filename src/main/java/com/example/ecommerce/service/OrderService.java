package com.example.ecommerce.service;

import com.example.ecommerce.exception.EcommerceApplicationException;
import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.exception.UnathorizedException;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.model.PaymentMethod;
import com.example.ecommerce.model.ShippingMethod;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymetnMethodRepository;
import com.example.ecommerce.repository.ShippingMethodRepository;
import com.example.ecommerce.request.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final PaymetnMethodRepository paymentMethodRepository;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository,
                         ShippingMethodRepository shippingMethodRepository, PaymetnMethodRepository paymentMethodRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.shippingMethodRepository = shippingMethodRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public Order placeOrder(User user, OrderRequest request) {
        List<Cart> items = cartRepository.findByUserId(user.getId());
        if (items.isEmpty()) {
            throw new EcommerceApplicationException("A kosár üres, nem lehet rendelést leadni.");
        }

        ShippingMethod shippingMethod = shippingMethodRepository.findById(request.getShippingMethodId())
                .orElseThrow(() -> new EntityNotFoundException("Szállítási mód nem található."));
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new EntityNotFoundException("Fizetési mód nem található."));

        Order order = new Order();
        order.setOrderDate(Instant.now());
        order.setOrderAddress(request.getAddress());
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);
        order.setShippingMethod(shippingMethod);
        order.setPaymentMethod(paymentMethod);

        List<OrderItem> orderItems = items.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            return orderItem;
        }).toList();
        order.setItems(orderItems);

        orderRepository.save(order);
        cartRepository.deleteAll(items);
        return order;
    }

    public Page<Order> findMyOrders(User user, Pageable pageable) {
        return orderRepository.findByUserId(user.getId(), pageable);
    }

    public Order findById(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Rendelés nem található."));
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));
        if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
            throw new UnathorizedException("Ez a rendelés nem a bejelentkezett felhasználóhoz tartozik.");
        }
        return order;
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Rendelés nem található."));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
