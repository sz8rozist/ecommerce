package com.example.ecommerce.service;

import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.repository.ProductImageRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.request.ProductRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    private final MinioService minioService;

    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository, MinioService minioService) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.minioService = minioService;
    }

    public Page<Product> findALl(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());

        List<ProductImage> images = new ArrayList<>();

        for (MultipartFile file : productRequest.getImages()) {
            String imageUrl = minioService.uploadFile(file);
            ProductImage image = new ProductImage();
            image.setImageUrl(imageUrl);
            image.setProduct(product);
            images.add(image);
        }

        product.setImages(images);
        return productRepository.save(product);
    }
}
