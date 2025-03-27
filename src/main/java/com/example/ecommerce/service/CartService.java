package com.example.ecommerce.service;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public Cart addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow();
        Cart item = new Cart();
        item.setProduct(product);
        item.setQuantity(quantity);
        return cartRepository.save(item);
    }
}
