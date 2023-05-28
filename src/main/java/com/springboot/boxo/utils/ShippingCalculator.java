package com.springboot.boxo.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ShippingCalculator {
    public static double calculateShippingValue(double distance) {
        double baseRate = 10.0; // base rate per km
        double ratePerKm = 0.3; // rate per km
        double shippingCost = 0.0;

        // Calculate shipping cost based on distance using if-else statements
        if (distance >= 10 && distance < 30) {
            shippingCost = baseRate + distance * ratePerKm * 0.8;
        } else if (distance >= 30 && distance < 100) {
            shippingCost = baseRate + distance * ratePerKm * 0.6;
        } else if (distance >= 100) {
            shippingCost = baseRate + distance * ratePerKm * 0.5;
        } else if (distance >= 0) {
            shippingCost = baseRate + distance * ratePerKm;
        }

        shippingCost = Math.ceil(shippingCost);

        return shippingCost;
    }
}