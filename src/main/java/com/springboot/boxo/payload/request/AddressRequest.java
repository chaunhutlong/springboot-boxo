package com.springboot.boxo.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {
    private String name;
    private String phone;
    private Long cityId;
    private String description;
    private boolean isDefault;
    private double distance;
}
