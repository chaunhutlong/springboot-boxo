package com.springboot.boxo.payload.dto;

import com.springboot.boxo.entity.Discount;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderDTO extends IDTO {
    UserDTO user;
    List<ShortBookDTO> books;
    private double totalPayment;
    private Discount discount;

    private String status;
    private ShippingDTO shipping;
    private PaymentDTO payment;
}