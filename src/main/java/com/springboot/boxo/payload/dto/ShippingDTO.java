package com.springboot.boxo.payload.dto;

import com.springboot.boxo.enums.ShippingStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ShippingDTO extends IDTO
{
    private Long id;
    private AddressDTO address;
    private double value;
    private String trackingNumber;
    private Long orderId;

    private ShippingStatus status;
}
