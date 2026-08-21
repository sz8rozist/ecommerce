package com.example.ecommerce.service;

import com.example.ecommerce.exception.EcommerceApplicationException;
import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.exception.UnathorizedException;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Coupon;
import com.example.ecommerce.model.Discount;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.model.PaymentMethod;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ShippingMethod;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.DiscountRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymetnMethodRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ShippingMethodRepository;
import com.example.ecommerce.request.OrderRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final PaymetnMethodRepository paymentMethodRepository;
    private final DiscountRepository discountRepository;
    private final CouponService couponService;
    private final ProductRepository productRepository;
    private final JavaMailSender mailSender;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository,
                         ShippingMethodRepository shippingMethodRepository, PaymetnMethodRepository paymentMethodRepository,
                         DiscountRepository discountRepository, CouponService couponService, ProductRepository productRepository,
                         JavaMailSender mailSender) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.shippingMethodRepository = shippingMethodRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.discountRepository = discountRepository;
        this.couponService = couponService;
        this.productRepository = productRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public Order placeOrder(User user, OrderRequest request) {
        List<Cart> items = cartRepository.findByUserId(user.getId());
        if (items.isEmpty()) {
            throw new EcommerceApplicationException("A kosár üres, nem lehet rendelést leadni.");
        }

        for (Cart item : items) {
            if (item.getQuantity() > item.getProduct().getStockQuantity()) {
                throw new EcommerceApplicationException(
                        "Nincs elég készleten a(z) \"" + item.getProduct().getName() + "\" termékből.");
            }
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

        LocalDate today = LocalDate.now();
        List<OrderItem> orderItems = items.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(effectivePrice(cartItem, today));
            orderItem.setOrder(order);
            return orderItem;
        }).toList();
        order.setItems(orderItems);

        if (StringUtils.hasText(request.getCouponCode())) {
            Coupon coupon = couponService.validate(request.getCouponCode());
            double itemsTotal = orderItems.stream()
                    .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                    .sum();
            order.setCouponCode(coupon.getCode());
            order.setDiscountAmount(itemsTotal * coupon.getPercentage() / 100.0);
        }

        for (Cart item : items) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        orderRepository.save(order);
        cartRepository.deleteAll(items);

        sendOrderConfirmationEmail(order);

        return order;
    }

    private double effectivePrice(Cart cartItem, LocalDate today) {
        return discountRepository.findActiveByProductId(cartItem.getProduct().getId(), today)
                .map((Discount discount) -> cartItem.getProduct().getPrice() * (100 - discount.getPercentage()) / 100.0)
                .orElse(cartItem.getProduct().getPrice());
    }

    private void sendOrderConfirmationEmail(Order order) {
        String email = order.getUser().getEmail();
        if (!StringUtils.hasText(email)) {
            return;
        }

        try {
            StringBuilder body = new StringBuilder();
            body.append("<h2>Köszönjük a rendelésed!</h2>");
            body.append("<p>Rendelés azonosító: #").append(order.getId()).append("</p>");
            body.append("<p>Szállítási cím: ").append(order.getOrderAddress()).append("</p>");
            body.append("<table border=\"0\" cellpadding=\"6\">");
            body.append("<tr><th align=\"left\">Termék</th><th align=\"left\">Mennyiség</th><th align=\"left\">Egységár</th></tr>");
            double itemsTotal = 0;
            for (OrderItem item : order.getItems()) {
                double lineTotal = item.getUnitPrice() * item.getQuantity();
                itemsTotal += lineTotal;
                body.append("<tr><td>").append(item.getProduct().getName()).append("</td><td>")
                        .append(item.getQuantity()).append("</td><td>")
                        .append((int) item.getUnitPrice()).append(" Ft</td></tr>");
            }
            body.append("</table>");
            if (order.getDiscountAmount() > 0) {
                body.append("<p>Kuponkedvezmény (").append(order.getCouponCode()).append("): -")
                        .append((int) order.getDiscountAmount()).append(" Ft</p>");
            }
            double shippingPrice = order.getShippingMethod() != null ? order.getShippingMethod().getPrice() : 0;
            double total = itemsTotal - order.getDiscountAmount() + shippingPrice;
            body.append("<p><strong>Végösszeg: ").append((int) total).append(" Ft</strong></p>");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Rendelés visszaigazolás - #" + order.getId());
            helper.setText(body.toString(), true);
            mailSender.send(message);
        } catch (MessagingException | RuntimeException e) {
            log.error("Hiba történt a rendelés visszaigazoló email küldése közben.", e);
        }
    }

    public Page<Order> findMyOrders(User user, Pageable pageable) {
        return orderRepository.findByUserId(user.getId(), pageable);
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Page<Order> findAll(Pageable pageable, OrderStatus status) {
        if (status == null) {
            return orderRepository.findAll(pageable);
        }
        return orderRepository.findByStatus(status, pageable);
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

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Rendelés nem található."));

        if (status == OrderStatus.CANCELED && order.getStatus() != OrderStatus.CANCELED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order updateTrackingNumber(Long orderId, String trackingNumber) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Rendelés nem található."));
        order.setTrackingNumber(trackingNumber);
        return orderRepository.save(order);
    }
}
