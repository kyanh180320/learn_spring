package com.example.learn_spring.mapper;

import com.example.learn_spring.dto.request.CustomerRequest;
import com.example.learn_spring.dto.response.CustomerResponse;
import com.example.learn_spring.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request) {
        if (request == null) {
            return null;
        }
        return Customer.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();
    }

    public CustomerResponse toResponse(Customer entity) {
        if (entity == null) {
            return null;
        }
        return CustomerResponse.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .build();
    }

    public void updateEntity(Customer entity, CustomerRequest request) {
        if (entity == null || request == null) {
            return;
        }
        entity.setFullName(request.getFullName());
        entity.setEmail(request.getEmail());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setAddress(request.getAddress());
    }
}
