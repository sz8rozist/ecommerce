package com.example.ecommerce.response;

import com.example.ecommerce.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EcommerceApiMapper {
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponseDTO toProductDto(Product product);
}
