package com.sensors.model;

/**
 * A single anomaly detection reported by a sensor.
 *
 * @param id        identifier assigned by the sensor (unique per sensor only)
 * @param latitude  latitude in decimal degrees (WGS 84)
 * @param longitude longitude in decimal degrees (WGS 84)
 */
public record SensorReading(int id, double latitude, double longitude) {

    /**
     * Checks that the coordinates fall within the valid WGS 84 range.
     *
     * @return {@code true} if latitude is in [-90, 90] and longitude is in [-180, 180]
     */
    public boolean hasValidCoordinates() {
        return latitude >= -90 && latitude <= 90
            && longitude >= -180 && longitude <= 180;
    }
}