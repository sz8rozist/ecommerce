package com.example.ecommerce.response;

import com.example.ecommerce.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EcommerceApiMapper {
    @Mapping(target = "imageUrls", ignore = true)
    ProductResponseDTO toProductDto(Product product);
}
