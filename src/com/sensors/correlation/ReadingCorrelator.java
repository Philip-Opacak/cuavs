package com.sensors.correlation;

import com.sensors.geo.DistanceCalculator;
import com.sensors.model.Match;
import com.sensors.model.SensorReading;
import java.util.*;

/**
 * Correlates readings from two sensors, pairing those that are within a maximum
 * distance of each other and are therefore likely to be the same real anomaly.
 *
 * <p>The second sensor's readings are indexed by latitude so that each reading of
 * the first sensor only needs to be compared against a narrow latitude band
 * instead of the whole data set.</p>
 */
public class ReadingCorrelator {

    /** Conservative lower bound for the length of one degree of latitude. */
    private static final double METERS_PER_DEGREE_LATITUDE = 110_000;

    private final DistanceCalculator distanceCalculator;
    private final double maxDistanceMeters;

    /**
     * Creates a correlator.
     *
     * @param distanceCalculator strategy used to measure distances between readings
     * @param maxDistanceMeters  maximum distance for two readings to be considered a match
     */
    public ReadingCorrelator(DistanceCalculator distanceCalculator, double maxDistanceMeters) {
        this.distanceCalculator = distanceCalculator;
        this.maxDistanceMeters = maxDistanceMeters;
    }

    /**
     * Finds the readings of both sensors that refer to the same anomaly.
     *
     * @param first  readings from the first sensor
     * @param second readings from the second sensor
     * @return a map from first-sensor id to second-sensor id, in order of increasing distance
     */
    public Map<Integer, Integer> correlate(List<SensorReading> first, List<SensorReading> second) {
        NavigableMap<Double, List<SensorReading>> secondByLatitude = indexByLatitude(valid(second));
        List<Match> candidates = findCandidates(valid(first), secondByLatitude);
        return selectOneToOne(candidates);
    }

    /**
     * Collects every pair of readings that lies within the maximum distance.
     *
     * @param first            valid readings of the first sensor
     * @param secondByLatitude valid readings of the second sensor, indexed by latitude
     * @return all candidate matches, unordered
     */
    private List<Match> findCandidates(List<SensorReading> first,
                                       NavigableMap<Double, List<SensorReading>> secondByLatitude) {
        double latitudeWindow = maxDistanceMeters / METERS_PER_DEGREE_LATITUDE;
        List<Match> candidates = new ArrayList<>();

        for (SensorReading a : first) {
            var nearbyBand = secondByLatitude.subMap(
                a.latitude() - latitudeWindow, true, a.latitude() + latitudeWindow, true);

            for (List<SensorReading> bucket : nearbyBand.values()) {
                for (SensorReading b : bucket) {
                    double distance = distanceCalculator.distanceMeters(a, b);
                    if (distance <= maxDistanceMeters) {
                        candidates.add(new Match(a.id(), b.id(), distance));
                    }
                }
            }
        }
        return candidates;
    }

    /**
     * Greedily picks the closest candidates so that no reading appears in more than one pair.
     *
     * @param candidates all candidate matches
     * @return the selected pairs as first-sensor id to second-sensor id
     */
    private Map<Integer, Integer> selectOneToOne(List<Match> candidates) {
        candidates.sort(Comparator.comparingDouble(Match::distanceMeters));

        Set<Integer> usedFirst = new HashSet<>();
        Set<Integer> usedSecond = new HashSet<>();
        Map<Integer, Integer> result = new LinkedHashMap<>();

        for (Match match : candidates) {
            if (usedFirst.contains(match.firstId()) || usedSecond.contains(match.secondId())) {
                continue;
            }
            usedFirst.add(match.firstId());
            usedSecond.add(match.secondId());
            result.put(match.firstId(), match.secondId());
        }
        return result;
    }

    /**
     * Removes readings whose coordinates are outside the WGS 84 range.
     *
     * @param readings readings to filter
     * @return only the readings with valid coordinates
     */
    private List<SensorReading> valid(List<SensorReading> readings) {
        return readings.stream().filter(SensorReading::hasValidCoordinates).toList();
    }

    /**
     * Groups readings by latitude in a sorted structure to allow range lookups.
     *
     * @param readings readings to index
     * @return readings keyed by latitude
     */
    private NavigableMap<Double, List<SensorReading>> indexByLatitude(List<SensorReading> readings) {
        NavigableMap<Double, List<SensorReading>> index = new TreeMap<>();
        for (SensorReading reading : readings) {
            index.computeIfAbsent(reading.latitude(), key -> new ArrayList<>()).add(reading);
        }
        return index;
    }
}