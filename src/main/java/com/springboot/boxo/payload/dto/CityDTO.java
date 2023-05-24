package com.springboot.boxo.payload.dto;

import lombok.Data;

@Data
public class CityDTO {
    private Long id;
    private String name;
    private float latitude;
    private float longitude;
    private ProvinceDTO province;
}
