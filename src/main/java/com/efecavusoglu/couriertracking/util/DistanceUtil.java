package com.efecavusoglu.couriertracking.util;

public class DistanceUtil {
    private static final double EARTH_RADIUS_KM = 6371.0;

    private DistanceUtil() {
        // constructor is private -- prevent instantiation of the utility class
    }

    /**
     * Haversine formula to calculate the distance of two geo points.
     * @param lat1 latitute of the point1
     * @param lng1 longitude of the point1
     * @param lat2 latitute of the point2
     * @param lng2 longitude of the point2
     * @return distance in meters
     */
    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double latitude1InRadians = Math.toRadians(lat1);
        double longitude1InRadians = Math.toRadians(lng1);
        double latitude2InRadians = Math.toRadians(lat2);
        double longitude2InRadians = Math.toRadians(lng2);

        double deltaLatitude = latitude2InRadians - latitude1InRadians;
        double deltaLongitude = longitude2InRadians - longitude1InRadians;

        double a = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2) +
                Math.cos(latitude1InRadians) * Math.cos(latitude2InRadians) * Math.sin(deltaLongitude / 2) * Math.sin(deltaLongitude / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c * 1000;
    }
}
