package com.springboot.boxo.controller;

import com.springboot.boxo.payload.dto.CityDTO;
import com.springboot.boxo.payload.dto.ProvinceDTO;
import com.springboot.boxo.service.ProvinceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("${spring.data.rest.base-path}/provinces")

public class ProvinceController {
    private final ProvinceService provinceService;

    public ProvinceController(ProvinceService provinceService) {
        this.provinceService = provinceService;
    }

    @GetMapping
    public ResponseEntity<List<ProvinceDTO>> getAllProvinces() {
        return ResponseEntity.ok(provinceService.getAllProvinces());
    }

    @GetMapping("/{provinceId}/cities")
    public ResponseEntity<List<CityDTO>> getCitiesByProvinceId(@PathVariable Long provinceId) {
        return ResponseEntity.ok(provinceService.getCitiesByProvinceId(provinceId));
    }
}
