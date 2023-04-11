package com.springboot.boxo.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DiscountType {
    PERCENTAGE("Percentage"),
    FIXED_AMOUNT("Fixed Amount");

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }
}