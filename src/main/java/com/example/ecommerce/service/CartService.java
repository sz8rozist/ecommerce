package com.example.ecommerce.service;

import com.example.ecommerce.exception.EcommerceApplicationException;
import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.exception.UnathorizedException;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public List<Cart> getCart(User user) {
        return cartRepository.findByUserId(user.getId());
    }

    public Cart addToCart(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new EcommerceApplicationException("A mennyiségnek pozitívnak kell lennie.", "quantity");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Termék nem található: " + productId));

        Cart item = cartRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseGet(() -> {
                    Cart newItem = new Cart();
                    newItem.setUser(user);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });
        int newQuantity = item.getQuantity() + quantity;
        if (newQuantity > product.getStockQuantity()) {
            throw new EcommerceApplicationException("Nincs elég készleten a termékből.", "quantity");
        }
        item.setQuantity(newQuantity);
        return cartRepository.save(item);
    }

    public Cart updateQuantity(User user, Long cartId, int quantity) {
        if (quantity <= 0) {
            throw new EcommerceApplicationException("A mennyiségnek pozitívnak kell lennie.", "quantity");
        }
        Cart item = getOwnedCartItem(user, cartId);
        if (quantity > item.getProduct().getStockQuantity()) {
            throw new EcommerceApplicationException("Nincs elég készleten a termékből.", "quantity");
        }
        item.setQuantity(quantity);
        return cartRepository.save(item);
    }

    public void removeFromCart(User user, Long cartId) {
        Cart item = getOwnedCartItem(user, cartId);
        cartRepository.delete(item);
    }

    private Cart getOwnedCartItem(User user, Long cartId) {
        Cart item = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Kosártétel nem található: " + cartId));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new UnathorizedException("Ez a kosártétel nem a bejelentkezett felhasználóhoz tartozik.");
        }
        return item;
    }
}
