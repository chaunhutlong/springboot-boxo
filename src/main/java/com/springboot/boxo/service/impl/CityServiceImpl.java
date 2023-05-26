package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.City;
import com.springboot.boxo.repository.CityRepository;
import com.springboot.boxo.service.CityService;
import com.springboot.boxo.utils.DistanceCalculator;
import org.springframework.stereotype.Service;

@Service
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private static final double LAT_DEFAULT = 10.87;
    private static final double LNG_DEFAULT = 106.8;
    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }
    @Override
    public double calculateDistance(Long cityId) {
        City city = cityRepository.findById(cityId).orElse(null);
        if (city != null) {
            return DistanceCalculator.getDistanceFromLatLonInKm(LAT_DEFAULT, LNG_DEFAULT, city.getLatitude(), city.getLongitude());
        }
        return 0.0;
    }
}