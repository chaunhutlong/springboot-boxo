package com.springboot.boxo.service;

import com.springboot.boxo.payload.dto.CityDTO;
import com.springboot.boxo.payload.dto.ProvinceDTO;

import java.util.List;

public interface ProvinceService {
    List<ProvinceDTO> getAllProvinces();
    List<CityDTO> getCitiesByProvinceId(Long provinceId);
}
