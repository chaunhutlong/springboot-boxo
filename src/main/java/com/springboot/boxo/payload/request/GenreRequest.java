package com.springboot.boxo.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreRequest {
    @NotEmpty(message = "Name should not be null or empty")
    private String name;
    private String description;

}
