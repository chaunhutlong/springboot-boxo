package com.springboot.boxo.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PaymentType {

    CASH_ON_DELIVERY("cash_on_delivery"),
    CREDIT_CARD("credit_card"),
    ONLINE_BANKING("online_banking"),
    PAYPAL("paypal");

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }
}
