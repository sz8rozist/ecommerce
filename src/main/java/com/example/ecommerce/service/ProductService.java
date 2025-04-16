package com.example.ecommerce.service;

import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.repository.ProductImageRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.request.ProductRequest;
import com.example.ecommerce.response.EcommerceApiMapper;
import com.example.ecommerce.response.ProductResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    private final EcommerceApiMapper mapper;

    private final MinioService minioService;

    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository, EcommerceApiMapper mapper, MinioService minioService) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.mapper = mapper;
        this.minioService = minioService;
    }

    public Page<Product> findALl(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        return productRepository.save(product);
    }

    @Transactional
    public Product uploadProductImages(Long productId, List<MultipartFile> files) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Termék nem található: " + productId));

        for (MultipartFile file : files) {
            String fileName = minioService.uploadFile(file);

            ProductImage image = new ProductImage();
            image.setImageUrl(fileName);
            image.setProduct(product);

            product.getImages().add(image);
        }

        return productRepository.save(product);
    }

    public ProductResponseDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Termék nem található: " + productId));
        ProductResponseDTO productResponseDTO = mapper.toProductDto(product);
        List<String> imageUrls = product.getImages().stream()
                .map(image -> minioService.getFileUrl(image.getImageUrl()))
                .toList();
        productResponseDTO.setImageUrls(imageUrls);
        return productResponseDTO;
    }
}
