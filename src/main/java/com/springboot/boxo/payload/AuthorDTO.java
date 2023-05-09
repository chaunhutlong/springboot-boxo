package com.springboot.boxo.payload;

import lombok.Data;

@Data
public class AuthorDTO {
    private Long id;
    private String name;
    private String bio;
    private String birthDate;
    private String deathDate;
}
