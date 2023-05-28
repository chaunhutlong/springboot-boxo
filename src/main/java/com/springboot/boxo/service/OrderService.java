package com.springboot.boxo.service;

import com.springboot.boxo.enums.ShippingStatus;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.OrderDTO;
import com.springboot.boxo.payload.dto.ShippingDTO;
import org.springframework.http.HttpStatus;

public interface OrderService {
    OrderDTO processPaymentOrder(Long userId, String paymentType, String discountCode);
    HttpStatus checkoutOrder(Long userId, Long orderId);
    HttpStatus cancelOrder(Long userId, Long orderId);
    OrderDTO getOrder(Long orderId);
    PaginationResponse<OrderDTO> getOrdersByUserId(Long userId, int pageNumber, int pageSize, String sortBy, String sortDir);
    PaginationResponse<OrderDTO> getAllOrders(int pageNumber, int pageSize, String sortBy, String sortDir);
    ShippingDTO getShippingByOrderId(Long orderId);
    HttpStatus updateShippingStatus(Long orderId, ShippingStatus status);
}
