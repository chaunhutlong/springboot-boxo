package com.springboot.boxo.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequest {
    @NotEmpty(message = "Name should not be null or empty")
    private String name;
    private String bio;
    @Past(message = "Birth date should be in the past")
    private String birthDate;
    @Past(message = "Death date should be in the past")
    private String deathDate;
}
