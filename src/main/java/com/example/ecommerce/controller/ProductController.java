package com.example.ecommerce.controller;

import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/{productId}/upload-image")
    public ProductImage uploadImage(@PathVariable Long productId, @RequestParam("file") MultipartFile file) throws IOException {
        return productService.uploadImage(productId, file);
    }
}
