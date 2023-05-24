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
    @NotEmpty(message = "Title should not be null or empty")
    private String title;
    @NotEmpty(message = "ISBN should not be null or empty")
    private String isbn;
    private String publicationDate;
    private String language;
    private String description;
    private int pages;
    private int quantity;
    private float price;
    private float priceDiscount;
    @NotEmpty(message = "Publisher should not be null or empty")
    private Long publisherId;
    @NotEmpty(message = "Authors should not be null or empty")
    private List<Long> authorIds;
    @NotEmpty(message = "Genres should not be null or empty")
    private List<Long> genreIds;
    private List<String> images;

}
