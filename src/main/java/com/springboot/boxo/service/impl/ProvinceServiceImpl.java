package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.City;
import com.springboot.boxo.entity.Province;
import com.springboot.boxo.payload.dto.CityDTO;
import com.springboot.boxo.payload.dto.ProvinceDTO;
import com.springboot.boxo.repository.CityRepository;
import com.springboot.boxo.repository.ProvinceRepository;
import com.springboot.boxo.service.ProvinceService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinceServiceImpl implements ProvinceService {
    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;
    private final ModelMapper modelMapper;

    public ProvinceServiceImpl(ProvinceRepository provinceRepository, CityRepository cityRepository, ModelMapper modelMapper) {
        this.provinceRepository = provinceRepository;
        this.cityRepository = cityRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ProvinceDTO> getAllProvinces() {
        var provinces = provinceRepository.findAll();
        return provinces.stream().map(this::mapToDTO).toList();
    }

    @Override
    public List<CityDTO> getCitiesByProvinceId(Long provinceId) {
        return cityRepository.findByProvinceId(provinceId).stream().map(this::mapToDTO).toList();
    }

    private ProvinceDTO mapToDTO(Province province) {
        return modelMapper.map(province, ProvinceDTO.class);
    }

    private CityDTO mapToDTO(City city) {
        return modelMapper.map(city, CityDTO.class);
    }
}