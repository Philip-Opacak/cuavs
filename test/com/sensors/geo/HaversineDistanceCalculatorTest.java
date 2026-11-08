package test.com.sensors.geo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sensors.geo.DistanceCalculator;
import com.sensors.geo.HaversineDistanceCalculator;
import com.sensors.model.SensorReading;
import org.junit.jupiter.api.Test;

/** Tests for {@link HaversineDistanceCalculator}. */
class HaversineDistanceCalculatorTest {

    private final DistanceCalculator calculator = new HaversineDistanceCalculator();

    @Test
    void distanceToSamePointIsZero() {
        SensorReading point = new SensorReading(1, 51.0, -114.0);

        assertEquals(0.0, calculator.distanceMeters(point, point), 1e-9);
    }

    @Test
    void thousandthOfADegreeOfLatitudeIsAbout111Meters() {
        SensorReading a = new SensorReading(1, 51.000, -114.0);
        SensorReading b = new SensorReading(2, 51.001, -114.0);

        assertEquals(111.2, calculator.distanceMeters(a, b), 0.5);
    }

    @Test
    void distanceIsSymmetric() {
        SensorReading a = new SensorReading(1, 51.0, -114.0);
        SensorReading b = new SensorReading(2, 51.5, -113.5);

        assertEquals(calculator.distanceMeters(a, b), calculator.distanceMeters(b, a), 1e-9);
    }
}