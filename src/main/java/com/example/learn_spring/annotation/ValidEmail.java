package com.example.learn_spring.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
// 2. Giữ lại annotation lúc runtime để Spring đọc và validate được
@Retention(RetentionPolicy.RUNTIME)
@Documented
// 3. Khai báo class chịu trách nhiệm thực thi logic kiểm tra
@Constraint(validatedBy = EmailValidator.class)
public @interface ValidEmail {
    // Thông báo lỗi mặc định khi email không hợp lệ
    String message() default "Email không đúng định dạng";
    // 2 thuộc tính bắt buộc phải có theo chuẩn của Jakarta Validation
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}