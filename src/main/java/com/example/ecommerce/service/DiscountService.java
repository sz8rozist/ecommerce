package com.example.ecommerce.service;

import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.model.Discount;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.DiscountRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.request.DiscountRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    public DiscountService(DiscountRepository discountRepository, ProductRepository productRepository) {
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
    }

    public List<Discount> findAll() {
        return discountRepository.findAll();
    }

    public List<Discount> findAllActive() {
        return discountRepository.findAllActive(LocalDate.now());
    }

    public Optional<Discount> getActiveDiscount(Long productId) {
        return discountRepository.findActiveByProductId(productId, LocalDate.now());
    }

    public Discount create(DiscountRequest request) {
        Discount discount = new Discount();
        applyRequest(discount, request);
        return discountRepository.save(discount);
    }

    public Discount update(Long id, DiscountRequest request) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Akció nem található."));
        applyRequest(discount, request);
        return discountRepository.save(discount);
    }

    private void applyRequest(Discount discount, DiscountRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Termék nem található: " + request.getProductId()));
        discount.setProduct(product);
        discount.setPercentage(request.getPercentage());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
    }

    public void delete(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Akció nem található."));
        discountRepository.delete(discount);
    }
}
