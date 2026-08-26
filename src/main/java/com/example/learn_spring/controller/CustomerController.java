package com.example.learn_spring.controller;

import com.example.learn_spring.dto.api.ApiResponse;
import com.example.learn_spring.dto.request.CustomerRequest;
import com.example.learn_spring.dto.response.CustomerResponse;
import com.example.learn_spring.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Controller", description = "Quản lý khách hàng")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả khách hàng")
    public ApiResponse<List<CustomerResponse>> getAllCustomers() {
        return ApiResponse.success(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết khách hàng theo ID")
    public ApiResponse<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ApiResponse.success(customerService.getCustomerById(id));
    }

    @PostMapping
    @Operation(summary = "Tạo mới khách hàng")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo khách hàng thành công", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin khách hàng")
    public ApiResponse<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        return ApiResponse.success("Cập nhật khách hàng thành công", customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa khách hàng theo ID")
    public ApiResponse<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ApiResponse.success("Xóa khách hàng thành công", null);
    }
}
