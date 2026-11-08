package com.sensors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensors.correlation.ReadingCorrelator;
import com.sensors.geo.HaversineDistanceCalculator;
import com.sensors.io.CsvSensorReader;
import com.sensors.io.JsonSensorReader;
import com.sensors.model.SensorReading;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Entry point: reads both sensor files from the {@code data} directory, correlates
 * the readings, and prints the matched id pairs as JSON.
 */
public class Main {

    private static final Path DATA_DIR = Path.of("data");

    /**
     * Maximum distance between two readings of the same anomaly. Each sensor is
     * accurate to 100 m, so two sensors can disagree by up to 200 m.
     */
    private static final double MAX_DISTANCE_METERS = 200;

    /**
     * Runs the correlation.
     *
     * @param args unused
     * @throws Exception if a data file cannot be read or the result cannot be serialized
     */
    public static void main(String[] args) throws Exception {
        List<SensorReading> sensor1 = new CsvSensorReader().read(DATA_DIR.resolve("SensorData1.csv"));
        List<SensorReading> sensor2 = new JsonSensorReader().read(DATA_DIR.resolve("SensorData2.json"));

        ReadingCorrelator  correlator = new ReadingCorrelator(new HaversineDistanceCalculator(), MAX_DISTANCE_METERS);
        Map<Integer, Integer> matches = correlator.correlate(sensor1, sensor2);

        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(matches));
    }
}