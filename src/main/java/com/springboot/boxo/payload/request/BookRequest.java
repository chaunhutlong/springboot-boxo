package com.springboot.boxo.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {
    @NotEmpty(message = "Name should not be null or empty")
    private String name;
    @NotEmpty(message = "ISBN should not be null or empty")
    private String isbn;
    private String publishedDate;
    private String language;
    private String description;
    private int totalPages;

    @NotEmpty(message = "Available quantity should not be null or empty")
    private int availableQuantity;

    @NotEmpty(message = "Price should not be null or empty")
    private double price;

    private Double priceDiscount;
    private Long publisherId;
    private List<Long> authors;
    private List<Long> genres;
    private List<String> images;

}
