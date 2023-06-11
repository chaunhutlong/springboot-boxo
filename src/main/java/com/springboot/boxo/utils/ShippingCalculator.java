package com.springboot.boxo.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ShippingCalculator {
    public static double calculateShippingValue(double distance) {
        double baseRate = 10.0; // base rate per km
        double ratePerKm = 0.3; // rate per km
        double shippingCost;

        // Calculate shipping cost based on distance using if-else statements
        if (distance < 10) {
            shippingCost = baseRate + distance * ratePerKm;
        } else if (distance >= 10 && distance < 30) {
            shippingCost = baseRate + distance * ratePerKm * 0.8;
        } else if (distance >= 30 && distance < 100) {
            shippingCost = baseRate + distance * ratePerKm * 0.6;
        } else if (distance >= 100 && distance < 300) {
            shippingCost = baseRate + distance * ratePerKm * 0.5;
        } else if (distance >= 300) {
            shippingCost = 60;
        } else {
            shippingCost = 0; // if distance is not provided or invalid, set shipping cost to 0
        }


        shippingCost = Math.ceil(shippingCost * 1000);

        return shippingCost;
    }
}