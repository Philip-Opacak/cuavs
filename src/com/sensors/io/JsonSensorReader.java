package com.sensors.io;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensors.model.SensorReading;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Reads sensor readings from a JSON file containing an array of
 * {@code {"id": ..., "latitude": ..., "longitude": ...}} objects.
 */
public class JsonSensorReader implements SensorReader {

    private final ObjectMapper mapper = new ObjectMapper();

    /** {@inheritDoc} */
    @Override
    public List<SensorReading> read(Path file) throws IOException {
        return mapper.readValue(file.toFile(), new TypeReference<>() { });
    }
}