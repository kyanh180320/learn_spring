package com.example.learn_spring.service;

import com.example.learn_spring.dto.request.ProductRequest;
import com.example.learn_spring.dto.response.ProductResponse;
import com.example.learn_spring.entity.Category;
import com.example.learn_spring.entity.Product;
import com.example.learn_spring.exception.AppException;
import com.example.learn_spring.exception.ErrorCode;
import com.example.learn_spring.mapper.ProductMapper;
import com.example.learn_spring.repository.CategoryRepository;
import com.example.learn_spring.repository.ProductRepository;
import com.example.learn_spring.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// ============================================================
// @ExtendWith(MockitoExtension.class)
// → Bật Mockito cho class test này
// → Mockito sẽ tự tạo các @Mock và @InjectMocks trước mỗi test
// → KHÔNG khởi động Spring context → chạy rất nhanh
// ============================================================
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    // ============================================================
    // @Mock → Tạo object GIẢ (fake)
    // → Không gọi database thật, không gọi logic thật
    // → Mọi method đều trả về null/0/false... nếu không được cấu hình
    // → Dùng when(...).thenReturn(...) để giả lập hành vi
    // ============================================================

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getProductById_Success(){
        Product mockProduct = Product.builder().id(1L).name("Ban phim").build();
        ProductResponse mockResponse = ProductResponse.builder().id(1L).name("Ban phim").build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        when(productMapper.toResponse(mockProduct)).thenReturn(mockResponse);

        ProductResponse result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Ban phim");
    }


}
