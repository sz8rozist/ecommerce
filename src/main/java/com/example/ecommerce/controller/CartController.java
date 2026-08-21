package com.example.ecommerce.controller;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Cart>> getCart() {
        return ResponseEntity.ok(cartService.getCart(userService.getAuthenticatedUser()));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        Cart cart = cartService.addToCart(userService.getAuthenticatedUser(), productId, quantity);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cart> updateQuantity(@PathVariable Long id, @RequestParam int quantity) {
        Cart cart = cartService.updateQuantity(userService.getAuthenticatedUser(), id, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(userService.getAuthenticatedUser(), id);
    }
}
