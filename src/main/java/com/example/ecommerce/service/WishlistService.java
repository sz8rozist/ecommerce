package com.example.ecommerce.service;

import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.exception.UnathorizedException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.WishlistItem;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.WishlistItemRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class WishlistService {
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistItemRepository wishlistItemRepository, ProductRepository productRepository) {
        this.wishlistItemRepository = wishlistItemRepository;
        this.productRepository = productRepository;
    }

    public List<WishlistItem> getWishlist(User user) {
        return wishlistItemRepository.findByUserIdOrderByAddedAtDesc(user.getId());
    }

    public WishlistItem addToWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Termék nem található: " + productId));

        return wishlistItemRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseGet(() -> {
                    WishlistItem item = new WishlistItem();
                    item.setUser(user);
                    item.setProduct(product);
                    item.setAddedAt(Instant.now());
                    return wishlistItemRepository.save(item);
                });
    }

    public void removeFromWishlist(User user, Long id) {
        WishlistItem item = wishlistItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kívánságlista-tétel nem található: " + id));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new UnathorizedException("Ez a kívánságlista-tétel nem a bejelentkezett felhasználóhoz tartozik.");
        }
        wishlistItemRepository.delete(item);
    }
}
