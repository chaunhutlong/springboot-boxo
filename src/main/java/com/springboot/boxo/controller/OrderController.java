package com.springboot.boxo.controller;

import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.OrderDTO;
import com.springboot.boxo.payload.dto.ShippingDTO;
import com.springboot.boxo.payload.request.CheckoutOrderRequest;
import com.springboot.boxo.payload.request.ProcessPaymentOrderRequest;
import com.springboot.boxo.security.CustomUserDetails;
import com.springboot.boxo.service.OrderService;
import com.springboot.boxo.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/checkout")
    public ResponseEntity<HttpStatus> checkout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CheckoutOrderRequest checkoutOrderRequest) {

        HttpStatus statusCode = orderService.checkoutOrder(userDetails.getUserId(), checkoutOrderRequest.getOrderId());
        return ResponseEntity.status(statusCode).build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/cancel")
    public ResponseEntity<HttpStatus> cancel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CheckoutOrderRequest checkoutOrderRequest) {

        HttpStatus statusCode = orderService.cancelOrder(userDetails.getUserId(), checkoutOrderRequest.getOrderId());
        return ResponseEntity.status(statusCode).build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(
            @PathVariable Long orderId) {

        var order = orderService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping
    public ResponseEntity<PaginationResponse<OrderDTO>> getOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir)
    {
        var order = orderService.getOrdersByUserId(userDetails.getUserId(), pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<PaginationResponse<OrderDTO>> getAllOrders(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir)
    {
        var order = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{orderId}/shipping")
    public ResponseEntity<ShippingDTO> getShippingByOrderId(
            @PathVariable Long orderId) {

        var shipping = orderService.getShippingByOrderId(orderId);
        return ResponseEntity.ok(shipping);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{orderId}/shipping")
    public ResponseEntity<HttpStatus> updateShippingStatus(
            @PathVariable Long orderId,
            @RequestBody ShippingDTO shippingDTO) {

        HttpStatus statusCode = orderService.updateShippingStatus(orderId, shippingDTO.getStatus());
        return ResponseEntity.status(statusCode).build();
    }
}
