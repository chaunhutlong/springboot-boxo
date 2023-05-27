package com.springboot.boxo.payload.dto;

import com.springboot.boxo.enums.PaymentType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PaymentDTO extends IDTO {
    private Long id;
    private boolean paid;

    private PaymentType paymentType;

    private double value;
    private Long orderId;
}