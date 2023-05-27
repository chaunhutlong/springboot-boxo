package com.springboot.boxo.payload.dto;

import lombok.Data;

import java.util.Set;

@Data
public class BookDTO {
    private Long id;
    private String name;
    private String isbn;
    private String publishedDate;
    private String language;
    private String description;
    private int totalPages;
    private int availableQuantity;
    private double price;
    private Double priceDiscount;
    private PublisherDTO publisher;
    private Set<AuthorDTO> authors;
    private Set<GenreDTO> genres;
    private Set<ImageDTO> images;
}
