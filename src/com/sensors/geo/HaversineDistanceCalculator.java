package com.sensors.geo;

import com.sensors.model.SensorReading;

/**
 * Computes great-circle distance using the Haversine formula, which is accurate
 * enough for the sub-kilometer distances this application cares about.
 */
public class HaversineDistanceCalculator implements DistanceCalculator {

    private static final double EARTH_RADIUS_METERS = 6_371_000;

    /** {@inheritDoc} */
    @Override
    public double distanceMeters(SensorReading a, SensorReading b) {
        double lat1 = Math.toRadians(a.latitude());
        double lat2 = Math.toRadians(b.latitude());
        double deltaLat = lat2 - lat1;
        double deltaLon = Math.toRadians(b.longitude() - a.longitude());

        double h = Math.pow(Math.sin(deltaLat / 2), 2)
                 + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(deltaLon / 2), 2);
        return 2 * EARTH_RADIUS_METERS * Math.asin(Math.sqrt(h));
    }
}