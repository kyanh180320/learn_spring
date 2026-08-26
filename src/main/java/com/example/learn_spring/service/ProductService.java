package com.example.learn_spring.service;

import com.example.learn_spring.dto.request.ProductRequest;
import com.example.learn_spring.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    List<ProductResponse> getProductsByCategory(Long categoryId);
    List<ProductResponse> searchProductsByName(String name);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
