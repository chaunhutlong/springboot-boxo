package com.springboot.boxo.payload.dto;

import lombok.Data;

@Data
public class CityDTO {
    private Long id;
    private String name;
    private double latitude;
    private double longitude;
    private ProvinceDTO province;
}
