package com.springboot.boxo.payload.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ProfileDTO {
    private Long id;
    private String biography;
    private String avatar;

    private Set<AddressDTO> addresses;
}
