package com.example.learn_spring.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Mã lỗi không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(1002, "Dữ liệu nhập vào không hợp lệ", HttpStatus.BAD_REQUEST),
    
    // Category errors
    CATEGORY_NOT_FOUND(2001, "Không tìm thấy danh mục", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_EXISTED(2002, "Tên danh mục đã tồn tại", HttpStatus.BAD_REQUEST),
    
    // Product errors
    PRODUCT_NOT_FOUND(3001, "Không tìm thấy sản phẩm", HttpStatus.NOT_FOUND),
    OUT_OF_STOCK(3002, "Số lượng sản phẩm trong kho không đủ", HttpStatus.BAD_REQUEST),
    
    // Customer errors
    CUSTOMER_NOT_FOUND(4001, "Không tìm thấy khách hàng", HttpStatus.NOT_FOUND),
    EMAIL_EXISTED(4002, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    
    // Order errors
    ORDER_NOT_FOUND(5001, "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND),
    EMPTY_ORDER_ITEMS(5002, "Đơn hàng phải có ít nhất 1 sản phẩm", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
