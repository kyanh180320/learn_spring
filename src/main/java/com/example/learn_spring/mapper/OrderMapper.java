package com.example.learn_spring.mapper;

import com.example.learn_spring.dto.response.OrderItemResponse;
import com.example.learn_spring.dto.response.OrderResponse;
import com.example.learn_spring.entity.Order;
import com.example.learn_spring.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderResponse toResponse(Order order, List<OrderItem> orderItems) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponse> itemResponses = orderItems == null 
                ? Collections.emptyList() 
                : orderItems.stream()
                    .map(orderItemMapper::toResponse)
                    .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerFullName(order.getCustomer() != null ? order.getCustomer().getFullName() : null)
                .customerEmail(order.getCustomer() != null ? order.getCustomer().getEmail() : null)
                .items(itemResponses)
                .build();
    }
}
