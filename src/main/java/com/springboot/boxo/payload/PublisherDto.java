package com.springboot.boxo.payload;

import lombok.Data;

@Data
public class PublisherDto {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
}
