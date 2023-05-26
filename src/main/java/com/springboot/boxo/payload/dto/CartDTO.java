package com.springboot.boxo.payload.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {
    private UserDTO user;
    private List<ItemCartDTO> items;
    private double totalPriceInCart;
}

