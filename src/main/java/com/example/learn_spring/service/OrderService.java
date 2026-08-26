package com.example.learn_spring.service;

import com.example.learn_spring.dto.request.OrderRequest;
import com.example.learn_spring.dto.response.OrderResponse;
import com.example.learn_spring.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getOrdersByCustomerId(Long customerId);
    OrderResponse createOrder(OrderRequest request);
    OrderResponse updateOrderStatus(Long id, OrderStatus status);
    void cancelOrder(Long id);
}
