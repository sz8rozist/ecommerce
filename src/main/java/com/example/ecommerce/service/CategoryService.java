package com.example.ecommerce.service;

import com.example.ecommerce.exception.EcommerceApplicationException;
import com.example.ecommerce.exception.EntityNotFoundException;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category create(Category category) {
        validateParent(category.getId(), category.getParentId());
        return categoryRepository.save(category);
    }

    public Category update(Category category) {
        Category existing = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new EntityNotFoundException("Kategória nem található."));
        validateParent(existing.getId(), category.getParentId());
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        existing.setParentId(category.getParentId());
        return categoryRepository.save(existing);
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kategória nem található."));
        if (productRepository.existsByCategoryId(id)) {
            throw new EcommerceApplicationException("A kategória nem törölhető, mert vannak hozzá tartozó termékek.");
        }
        if (categoryRepository.existsByParentId(id)) {
            throw new EcommerceApplicationException("A kategória nem törölhető, mert vannak hozzá tartozó alkategóriák.");
        }
        categoryRepository.delete(category);
    }

    private void validateParent(Long categoryId, Long parentId) {
        if (parentId == null) {
            return;
        }
        if (parentId.equals(categoryId)) {
            throw new EcommerceApplicationException("Egy kategória nem lehet saját maga alkategóriája.");
        }
        if (!categoryRepository.existsById(parentId)) {
            throw new EntityNotFoundException("A megadott szülőkategória nem található.");
        }
    }
}
