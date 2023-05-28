package com.springboot.boxo.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ShippingStatus {
    SHIPPING,
    PENDING,
    DELIVERED,
    CANCELLED
}
