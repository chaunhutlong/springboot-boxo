package com.springboot.boxo.controller;

import com.springboot.boxo.payload.dto.OrderDTO;
import com.springboot.boxo.payload.request.ProcessPaymentOrderRequest;
import com.springboot.boxo.security.CustomUserDetails;
import com.springboot.boxo.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${spring.data.rest.base-path}/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/payment")
    public ResponseEntity<OrderDTO> payment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ProcessPaymentOrderRequest processPaymentOrderRequest) {

        var order = orderService.processPaymentOrder(userDetails.getUserId(), processPaymentOrderRequest.getType(), processPaymentOrderRequest.getDiscountCode());
        return ResponseEntity.ok(order);
    }
}
