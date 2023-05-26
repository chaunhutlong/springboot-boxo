package com.springboot.boxo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.boxo.payload.DistanceResponse;
import com.springboot.boxo.payload.Route;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@UtilityClass
public class DistanceCalculator {
    private static final String API_URL = "https://router.project-osrm.org/route/v1/driving/{lon1},{lat1};{lon2},{lat2}";

    public static double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
        RestTemplate restTemplate = new RestTemplate();

        String apiUrl = API_URL.replace("{lat1}", String.valueOf(lat1))
                .replace("{lon1}", String.valueOf(lon1))
                .replace("{lat2}", String.valueOf(lat2))
                .replace("{lon2}", String.valueOf(lon2));

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            String json = response.getBody();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                DistanceResponse distanceResponse = objectMapper.readValue(json, DistanceResponse.class);
                List<Route> routes = distanceResponse.getRoutes();
                if (routes != null && !routes.isEmpty()) {
                    Route route = routes.get(0);
                    return route.getDistance() / 1000.0;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0.0;
    }
}
