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
import com.example.ecommerce.request.GuestOrderItemRequest;
import com.example.ecommerce.request.GuestOrderRequest;
import com.example.ecommerce.request.OrderFilter;
import com.example.ecommerce.request.OrderRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
            orderItem.setUnitPrice(effectivePrice(cartItem.getProduct(), today));
            orderItem.setOrder(order);
            return orderItem;
        }).toList();
        order.setItems(orderItems);

        applyCoupon(order, orderItems, request.getCouponCode());

        for (Cart item : items) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        orderRepository.save(order);
        cartRepository.deleteAll(items);

        sendOrderConfirmationEmail(order, user.getEmail());

        return order;
    }

    @Transactional
    public Order placeGuestOrder(GuestOrderRequest request) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(request.getShippingMethodId())
                .orElseThrow(() -> new EntityNotFoundException("Szállítási mód nem található."));
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new EntityNotFoundException("Fizetési mód nem található."));

        List<Product> products = request.getItems().stream()
                .map(item -> productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Termék nem található: " + item.getProductId())))
                .toList();

        for (int i = 0; i < request.getItems().size(); i++) {
            GuestOrderItemRequest itemRequest = request.getItems().get(i);
            Product product = products.get(i);
            if (itemRequest.getQuantity() > product.getStockQuantity()) {
                throw new EcommerceApplicationException(
                        "Nincs elég készleten a(z) \"" + product.getName() + "\" termékből.");
            }
        }

        Order order = new Order();
        order.setOrderDate(Instant.now());
        order.setOrderAddress(request.getAddress());
        order.setStatus(OrderStatus.PENDING);
        order.setGuestName(request.getGuestName());
        order.setGuestEmail(request.getGuestEmail());
        order.setGuestPhone(request.getGuestPhone());
        order.setShippingMethod(shippingMethod);
        order.setPaymentMethod(paymentMethod);

        LocalDate today = LocalDate.now();
        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < request.getItems().size(); i++) {
            GuestOrderItemRequest itemRequest = request.getItems().get(i);
            Product product = products.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(effectivePrice(product, today));
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        applyCoupon(order, orderItems, request.getCouponCode());

        for (Product product : products) {
            int quantity = request.getItems().stream()
                    .filter(item -> item.getProductId().equals(product.getId()))
                    .mapToInt(GuestOrderItemRequest::getQuantity)
                    .sum();
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
        }

        orderRepository.save(order);

        sendOrderConfirmationEmail(order, request.getGuestEmail());

        return order;
    }

    private void applyCoupon(Order order, List<OrderItem> orderItems, String couponCode) {
        if (StringUtils.hasText(couponCode)) {
            Coupon coupon = couponService.validate(couponCode);
            double itemsTotal = orderItems.stream()
                    .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                    .sum();
            order.setCouponCode(coupon.getCode());
            order.setDiscountAmount(itemsTotal * coupon.getPercentage() / 100.0);
        }
    }

    private double effectivePrice(Product product, LocalDate today) {
        return discountRepository.findActiveByProductId(product.getId(), today)
                .map((Discount discount) -> product.getPrice() * (100 - discount.getPercentage()) / 100.0)
                .orElse(product.getPrice());
    }

    private void sendOrderConfirmationEmail(Order order, String email) {
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

    public Page<Order> findAll(Pageable pageable, OrderFilter filter) {
        boolean noFilters = filter == null
                || (filter.getStatus() == null
                    && !StringUtils.hasText(filter.getUsername())
                    && filter.getFromDate() == null
                    && filter.getToDate() == null);
        if (noFilters) {
            return orderRepository.findAll(pageable);
        }
        return orderRepository.findAll(filterPredicate(filter), pageable);
    }

    private Specification<Order> filterPredicate(OrderFilter filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (filter.getStatus() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }

            if (StringUtils.hasText(filter.getUsername())) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(root.get("user").get("username"), "%" + filter.getUsername() + "%"));
            }

            if (filter.getFromDate() != null) {
                Instant from = filter.getFromDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), from));
            }

            if (filter.getToDate() != null) {
                Instant to = filter.getToDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThan(root.get("orderDate"), to));
            }

            return predicate;
        };
    }

    public Order findById(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Rendelés nem található."));
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()));
        boolean isOwner = order.getUser() != null && order.getUser().getId().equals(user.getId());
        if (!isAdmin && !isOwner) {
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
