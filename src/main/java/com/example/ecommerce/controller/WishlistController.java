package com.example.ecommerce.controller;

import com.example.ecommerce.model.WishlistItem;
import com.example.ecommerce.service.UserService;
import com.example.ecommerce.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    private final UserService userService;

    public WishlistController(WishlistService wishlistService, UserService userService) {
        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<WishlistItem>> getWishlist() {
        return ResponseEntity.ok(wishlistService.getWishlist(userService.getAuthenticatedUser()));
    }

    @PostMapping("/add")
    public ResponseEntity<WishlistItem> addToWishlist(@RequestParam Long productId) {
        WishlistItem item = wishlistService.addToWishlist(userService.getAuthenticatedUser(), productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromWishlist(@PathVariable Long id) {
        wishlistService.removeFromWishlist(userService.getAuthenticatedUser(), id);
    }
}
