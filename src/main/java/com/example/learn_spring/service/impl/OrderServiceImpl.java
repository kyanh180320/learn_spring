package com.example.learn_spring.service.impl;

import com.example.learn_spring.dto.request.OrderItemRequest;
import com.example.learn_spring.dto.request.OrderRequest;
import com.example.learn_spring.dto.response.OrderResponse;
import com.example.learn_spring.entity.*;
import com.example.learn_spring.exception.AppException;
import com.example.learn_spring.exception.ErrorCode;
import com.example.learn_spring.mapper.OrderItemMapper;
import com.example.learn_spring.mapper.OrderMapper;
import com.example.learn_spring.repository.CustomerRepository;
import com.example.learn_spring.repository.OrderItemRepository;
import com.example.learn_spring.repository.OrderRepository;
import com.example.learn_spring.repository.ProductRepository;
import com.example.learn_spring.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return orderMapper.toResponse(order, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return orderMapper.toResponse(order, items);
    }

    @Override
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new AppException(ErrorCode.CUSTOMER_NOT_FOUND);
        }
        return orderRepository.findByCustomerId(customerId).stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return orderMapper.toResponse(order, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_ORDER_ITEMS);
        }

        // Tạo và lưu đơn hàng ban đầu
        Order order = Order.builder()
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();
        Order savedOrder = orderRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            if (product.getQuantity() < itemReq.getQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            // Trừ số lượng tồn kho
            product.setQuantity(product.getQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            // Tạo chi tiết đơn hàng
            OrderItem orderItem = orderItemMapper.toEntity(savedOrder, product, itemReq.getQuantity());
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(orderItem.getTotalPrice());
        }

        // Lưu danh sách OrderItem qua OrderItemRepository
        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);

        // Cập nhật tổng tiền đơn hàng
        savedOrder.setTotalAmount(totalAmount);
        savedOrder = orderRepository.save(savedOrder);

        return orderMapper.toResponse(savedOrder, savedOrderItems);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return orderMapper.toResponse(updatedOrder, items);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.CANCELLED) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            // Hoàn lại số lượng tồn kho sản phẩm khi hủy đơn
            for (OrderItem item : items) {
                Product product = item.getProduct();
                if (product != null) {
                    product.setQuantity(product.getQuantity() + item.getQuantity());
                    productRepository.save(product);
                }
            }
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }
}
