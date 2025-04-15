package com.example.ecommerce.controller;

import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/{id}/upload-images")
    public ResponseEntity<List<ProductImage>> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files) throws IOException {

        List<ProductImage> images = productService.uploadImages(id, files);
        return ResponseEntity.ok(images);
    }
}
