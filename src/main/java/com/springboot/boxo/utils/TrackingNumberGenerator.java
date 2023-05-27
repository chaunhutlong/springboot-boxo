package com.springboot.boxo.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass

public class TrackingNumberGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random random = new Random();

    public static String generateTrackingNumber(int length) {
        StringBuilder trackingNumber = new StringBuilder("BOXO-");

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            trackingNumber.append(randomChar);
        }

        long timestamp = System.currentTimeMillis();
        String sequentialNumber = Long.toString(timestamp).substring(Math.max(0, Long.toString(timestamp).length() - 6));
        trackingNumber.append(sequentialNumber);

        return trackingNumber.toString();
    }
}
