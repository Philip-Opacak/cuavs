package com.sensors.io;

import com.sensors.model.SensorReading;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Reads sensor readings from a CSV file with the header {@code id,latitude,longitude}.
 */
public class CsvSensorReader implements SensorReader {

    /**
     * {@inheritDoc}
     * The first line is treated as a header and blank lines are ignored.
     */
    @Override
    public List<SensorReading> read(Path file) throws IOException {
        try (var lines = Files.lines(file)) {
            return lines.skip(1)
                        .filter(line -> !line.isBlank())
                        .map(this::parse)
                        .toList();
        }
    }

    /**
     * Parses one CSV data line into a reading.
     *
     * @param line a line formatted as {@code id,latitude,longitude}
     * @return the parsed reading
     */
    private SensorReading parse(String line) {
        String[] fields = line.split(",");
        return new SensorReading(
            Integer.parseInt(fields[0].trim()),
            Double.parseDouble(fields[1].trim()),
            Double.parseDouble(fields[2].trim()));
    }
}