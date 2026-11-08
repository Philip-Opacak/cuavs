package com.sensors.geo;

import com.sensors.model.SensorReading;

/**
 * Strategy for measuring the distance between two geographic readings.
 */
public interface DistanceCalculator {

    /**
     * Computes the distance between two readings.
     *
     * @param a first reading
     * @param b second reading
     * @return the distance in meters
     */
    double distanceMeters(SensorReading a, SensorReading b);
}