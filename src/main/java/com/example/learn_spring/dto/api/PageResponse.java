package com.example.learn_spring.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;

    private int pageNo;        // Trang hiện tại (1-indexed, thân thiện với client)
    private int pageSize;      // Số phần tử mỗi trang
    private long totalElements; // Tổng số phần tử
    private int totalPages;    // Tổng số trang
    private boolean isLast;    // Có phải trang cuối không
}