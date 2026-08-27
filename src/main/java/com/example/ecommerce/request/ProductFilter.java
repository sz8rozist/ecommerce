package com.example.ecommerce.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilter {
    private String name;
    private Long categoryId;
    private Double minPrice;
    private Double maxPrice;
    private Boolean inStockOnly;
}
