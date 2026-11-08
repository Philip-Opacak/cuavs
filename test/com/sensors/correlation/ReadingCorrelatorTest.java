package test.com.sensors.correlation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sensors.correlation.ReadingCorrelator;
import com.sensors.geo.HaversineDistanceCalculator;
import com.sensors.model.SensorReading;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Tests for {@link ReadingCorrelator}. */
class ReadingCorrelatorTest {

    private static final double MAX_DISTANCE_METERS = 200;

    private final ReadingCorrelator correlator =
        new ReadingCorrelator(new HaversineDistanceCalculator(), MAX_DISTANCE_METERS);

    private static SensorReading reading(int id, double latitude, double longitude) {
        return new SensorReading(id, latitude, longitude);
    }

    @Test
    void matchesReadingsWithinMaxDistance() {
        // 0.001 degrees of latitude is roughly 111 m
        List<SensorReading> first = List.of(reading(1, 51.000, -114.0));
        List<SensorReading> second = List.of(reading(7, 51.001, -114.0));

        Map<Integer, Integer> result = correlator.correlate(first, second);

        assertEquals(Map.of(1, 7), result);
    }

    @Test
    void ignoresReadingsBeyondMaxDistance() {
        // 0.003 degrees of latitude is roughly 333 m
        List<SensorReading> first = List.of(reading(1, 51.000, -114.0));
        List<SensorReading> second = List.of(reading(7, 51.003, -114.0));

        Map<Integer, Integer> result = correlator.correlate(first, second);

        assertTrue(result.isEmpty());
    }

    @Test
    void ignoresReadingsWithInvalidCoordinates() {
        List<SensorReading> first = List.of(reading(1, 150.0, 190.0));
        List<SensorReading> second = List.of(reading(7, 150.0, 190.0));

        Map<Integer, Integer> result = correlator.correlate(first, second);

        assertTrue(result.isEmpty());
    }

    @Test
    void pairsEachReadingAtMostOnceAndPrefersTheClosest() {
        // Both first readings are within range of the single second reading;
        // reading 1 is closer (about 56 m versus about 111 m).
        List<SensorReading> first = List.of(
            reading(1, 50.0000, -114.0),
            reading(2, 50.0015, -114.0));
        List<SensorReading> second = List.of(reading(9, 50.0005, -114.0));

        Map<Integer, Integer> result = correlator.correlate(first, second);

        assertEquals(Map.of(1, 9), result);
    }

    @Test
    void handlesIdsSharedBetweenSensors() {
        List<SensorReading> first = List.of(reading(1, 51.000, -114.0));
        List<SensorReading> second = List.of(reading(1, 51.001, -114.0));

        Map<Integer, Integer> result = correlator.correlate(first, second);

        assertEquals(Map.of(1, 1), result);
    }

    @Test
    void returnsEmptyResultForEmptyInput() {
        Map<Integer, Integer> result = correlator.correlate(List.of(), List.of());

        assertTrue(result.isEmpty());
    }
}