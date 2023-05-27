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
    @Pattern(regexp = "cash_on_delivery|credit_card|online_banking|paypal", message = "Invalid payment type")
    private String type;

    private String discountCode;
}