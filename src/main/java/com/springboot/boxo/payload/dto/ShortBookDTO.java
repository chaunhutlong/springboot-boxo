package com.springboot.boxo.payload.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ShortBookDTO extends IDTO {
    private Long bookId;
    private String name;
    private String isbn;
    private String publishedDate;
    private double price;
    private Double priceDiscount;
    private String imageUrl;
    private int quantity;
}
