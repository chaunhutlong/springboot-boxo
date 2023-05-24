package com.springboot.boxo.payload.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private Long id;
    private String name;
    private String phone;
    private float distance;
    private String description;
    private CityDTO city;
    private boolean isDefault;
    private UserDTO user;
}
