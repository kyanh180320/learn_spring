package com.example.learn_spring.mapper;

import com.example.learn_spring.dto.response.OrderItemResponse;
import com.example.learn_spring.entity.Order;
import com.example.learn_spring.entity.OrderItem;
import com.example.learn_spring.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderItemMapper {

    public OrderItem toEntity(Order order, Product product, Integer quantity) {
        BigDecimal unitPrice = product.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

        return OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .build();
    }

    public OrderItemResponse toResponse(OrderItem entity) {
        if (entity == null) {
            return null;
        }
        return OrderItemResponse.builder()
                .id(entity.getId())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .productName(entity.getProduct() != null ? entity.getProduct().getName() : null)
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalPrice(entity.getTotalPrice())
                .build();
    }
}
