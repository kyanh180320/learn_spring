package com.example.learn_spring.mapper;

import com.example.learn_spring.dto.request.ProductRequest;
import com.example.learn_spring.dto.response.ProductResponse;
import com.example.learn_spring.entity.Category;
import com.example.learn_spring.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request, Category category) {
        if (request == null) {
            return null;
        }
        return Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .description(request.getDescription())
                .category(category)
                .build();
    }

    public ProductResponse toResponse(Product entity) {
        if (entity == null) {
            return null;
        }
        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .quantity(entity.getQuantity())
                .description(entity.getDescription())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                .build();
    }

    public void updateEntity(Product entity, ProductRequest request, Category category) {
        if (entity == null || request == null) {
            return;
        }
        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity.setQuantity(request.getQuantity());
        entity.setDescription(request.getDescription());
        if (category != null) {
            entity.setCategory(category);
        }
    }
}
