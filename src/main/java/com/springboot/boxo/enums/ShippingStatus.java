package com.springboot.boxo.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ShippingStatus {
    PENDING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
