package com.springboot.boxo.payload;

import lombok.Data;

import java.util.Set;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private String isbn;
    private String publicationDate;
    private String language;
    private String description;
    private int pages;
    private int quantity;
    private float price;
    private float priceDiscount;
    private Long publisherId;
    private Set<Long> authorIds;
    private Set<Long> genreIds;

}
