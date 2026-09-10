package com.example.learn_spring.service;

import com.example.learn_spring.dto.api.PageResponse;
import com.example.learn_spring.dto.request.ProductRequest;
import com.example.learn_spring.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    PageResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String sortDir);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getProductsByCategory(Long categoryId);
    List<ProductResponse> searchProductsByName(String name);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
