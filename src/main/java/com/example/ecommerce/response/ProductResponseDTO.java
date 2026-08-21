package com.example.ecommerce.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private double price;
    private String description;
    private List<String> imageUrls;
    private Long categoryId;
    private String categoryName;
    private Integer discountPercentage;
    private Double discountedPrice;
    private int stockQuantity;
    private int minStockThreshold;
    private Double averageRating;
    private long reviewCount;
}
