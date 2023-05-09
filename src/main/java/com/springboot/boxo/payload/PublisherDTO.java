package com.springboot.boxo.payload;

import lombok.Data;

@Data
public class PublisherDTO {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private Integer booksCount = 0;
}
