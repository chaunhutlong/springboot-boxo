package com.springboot.boxo.payload.dto;

import lombok.Data;

@Data
public class ItemCartDTO {
    private Long bookId;
    private String name;
    private double price;
    private double priceDiscount;
    private String imageUrl;
    private int quantity;
    private boolean isChecked;
    private double totalPrice;
}
