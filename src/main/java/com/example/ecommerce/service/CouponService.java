package com.example.ecommerce.service;

import com.example.ecommerce.exception.EcommerceApplicationException;
import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.model.Coupon;
import com.example.ecommerce.repository.CouponRepository;
import com.example.ecommerce.request.CouponRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CouponService {
    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<Coupon> findAll() {
        return couponRepository.findAll();
    }

    public Coupon create(CouponRequest request) {
        Coupon coupon = new Coupon();
        applyRequest(coupon, request);
        return couponRepository.save(coupon);
    }

    public Coupon update(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kuponkód nem található."));
        applyRequest(coupon, request);
        return couponRepository.save(coupon);
    }

    private void applyRequest(Coupon coupon, CouponRequest request) {
        coupon.setCode(request.getCode().trim().toUpperCase());
        coupon.setPercentage(request.getPercentage());
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
    }

    public void delete(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kuponkód nem található."));
        couponRepository.delete(coupon);
    }

    public Coupon validate(String code) {
        Coupon coupon = couponRepository.findByCode(code.trim().toUpperCase())
                .orElseThrow(() -> new EcommerceApplicationException("Érvénytelen kuponkód.", "couponCode"));
        LocalDate today = LocalDate.now();
        if (today.isBefore(coupon.getStartDate()) || today.isAfter(coupon.getEndDate())) {
            throw new EcommerceApplicationException("A kuponkód nem érvényes.", "couponCode");
        }
        return coupon;
    }
}
