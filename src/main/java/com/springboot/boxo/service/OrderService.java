package com.springboot.boxo.service;

import com.springboot.boxo.payload.dto.OrderDTO;

public interface OrderService {
    OrderDTO processPaymentOrder(Long userId, String paymentType, String discountCode);
}
