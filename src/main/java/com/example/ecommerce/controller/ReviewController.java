package com.example.ecommerce.controller;

import com.example.ecommerce.model.Review;
import com.example.ecommerce.request.ReviewRequest;
import com.example.ecommerce.service.ReviewService;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping("/product/{id}/reviews")
    public ResponseEntity<Page<Review>> getReviews(@PathVariable Long id,
                                                     @RequestParam(required = false, defaultValue = "0") int page,
                                                     @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(reviewService.findByProduct(id, pageable));
    }

    @PostMapping("/product/{id}/reviews")
    public ResponseEntity<Review> createReview(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        Review review = reviewService.create(userService.getAuthenticatedUser(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
    }
}
