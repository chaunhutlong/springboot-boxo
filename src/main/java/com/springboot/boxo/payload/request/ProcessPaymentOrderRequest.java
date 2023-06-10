package com.springboot.boxo.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessPaymentOrderRequest {
    @NotNull(message = "Payment type must not be null")
    @Pattern(regexp = "CASH_ON_DELIVERY|CREDIT_CARD|ONLINE_BANKING|PAYPAL", message = "Invalid payment type")
    private String type;

    private String discountCode;
}