package com.sensors.io;

import com.sensors.model.SensorReading;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Reads sensor readings from a file. Each implementation handles one file format.
 */
public interface SensorReader {

    /**
     * Reads all readings from the given file.
     *
     * @param file path of the file to read
     * @return the readings contained in the file
     * @throws IOException if the file cannot be read or parsed
     */
    List<SensorReading> read(Path file) throws IOException;
}