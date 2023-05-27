package com.springboot.boxo.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OrderStatus {
    PENDING("Pending"),
    PAID("Paid"),
    CANCELLED("Cancelled");

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }
}
