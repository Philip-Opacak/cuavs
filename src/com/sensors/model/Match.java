package com.sensors.model;

/**
 * A candidate pairing between a reading of the first sensor and a reading of the second.
 *
 * @param firstId        id of the reading from the first sensor
 * @param secondId       id of the reading from the second sensor
 * @param distanceMeters distance between the two readings, in meters
 */
public record Match(int firstId, int secondId, double distanceMeters) { }