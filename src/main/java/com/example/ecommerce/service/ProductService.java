package com.example.ecommerce.service;

import com.example.ecommerce.exception.EcommerceApplicationException;
import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductImage;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.ProductImageRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.request.ProductFilter;
import com.example.ecommerce.request.ProductRequest;
import com.example.ecommerce.response.EcommerceApiMapper;
import com.example.ecommerce.response.ProductResponseDTO;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    private final CartRepository cartRepository;

    private final OrderItemRepository orderItemRepository;

    private final EcommerceApiMapper mapper;

    private final MinioService minioService;

    public ProductService(ProductRepository productRepository, ProductImageRepository productImageRepository,
                           CartRepository cartRepository, OrderItemRepository orderItemRepository,
                           EcommerceApiMapper mapper, MinioService minioService) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.cartRepository = cartRepository;
        this.orderItemRepository = orderItemRepository;
        this.mapper = mapper;
        this.minioService = minioService;
    }

    public Page<ProductResponseDTO> findALl(Pageable pageable, ProductFilter filter) {
        Page<Product> products = (filter == null || filter.getName() == null || filter.getName().isEmpty())
                ? productRepository.findAll(pageable)
                : productRepository.findAll(filterPredicate(filter), pageable);
        return products.map(this::toResponseDto);
    }

    private Specification<Product> filterPredicate(ProductFilter filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (filter.getName() != null && !filter.getName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("name"), "%" + filter.getName() + "%"));
            }
            return predicate;
        };
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
        return toResponseDto(product);
    }

    private ProductResponseDTO toResponseDto(Product product) {
        ProductResponseDTO productResponseDTO = mapper.toProductDto(product);
        List<String> imageUrls = product.getImages().stream()
                .map(image -> minioService.getFileUrl(image.getImageUrl()))
                .toList();
        productResponseDTO.setImageUrls(imageUrls);
        return productResponseDTO;
    }

    public Product updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Termék nem található: " + id));
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Termék nem található: " + id));

        if (orderItemRepository.existsByProductId(id)) {
            throw new EcommerceApplicationException("A termék nem törölhető, mert szerepel egy korábbi rendelésben.");
        }

        cartRepository.deleteByProductId(id);

        for (ProductImage image : product.getImages()) {
            minioService.deleteFile(image.getImageUrl());
        }

        productRepository.delete(product);
    }
}
