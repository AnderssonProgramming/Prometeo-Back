package edu.eci.cvds.prometeo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;




class BodyMeasurementsTest {

    @Test
    void testNoArgsConstructor() {
        BodyMeasurements measurements = new BodyMeasurements();
        assertNotNull(measurements);
        assertEquals(0.0, measurements.getHeight(), 0.001);
        assertEquals(0.0, measurements.getChestCircumference(), 0.001);
        assertEquals(0.0, measurements.getWaistCircumference(), 0.001);
        assertEquals(0.0, measurements.getHipCircumference(), 0.001);
        assertEquals(0.0, measurements.getBicepsCircumference(), 0.001);
        assertEquals(0.0, measurements.getThighCircumference(), 0.001);
        assertNotNull(measurements.getAdditionalMeasures());
    }

    @Test
    void testAllArgsConstructor() {
        Map<String, Double> additionalMeasures = new HashMap<>();
        additionalMeasures.put("calf", 40.0);
        
        BodyMeasurements measurements = new BodyMeasurements(
                170.0, 100.0, 80.0, 100.0, 35.0, 60.0, additionalMeasures
        );
        
        assertEquals(170.0, measurements.getHeight(), 0.001);
        assertEquals(100.0, measurements.getChestCircumference(), 0.001);
        assertEquals(80.0, measurements.getWaistCircumference(), 0.001);
        assertEquals(100.0, measurements.getHipCircumference(), 0.001);
        assertEquals(35.0, measurements.getBicepsCircumference(), 0.001);
        assertEquals(60.0, measurements.getThighCircumference(), 0.001);
        assertEquals(40.0, measurements.getAdditionalMeasures().get("calf"), 0.001);
    }

    @Test
    void testGettersAndSetters() {
        BodyMeasurements measurements = new BodyMeasurements();
        
        measurements.setHeight(180.0);
        assertEquals(180.0, measurements.getHeight(), 0.001);
        
        measurements.setChestCircumference(105.0);
        assertEquals(105.0, measurements.getChestCircumference(), 0.001);
        
        measurements.setWaistCircumference(85.0);
        assertEquals(85.0, measurements.getWaistCircumference(), 0.001);
        
        measurements.setHipCircumference(110.0);
        assertEquals(110.0, measurements.getHipCircumference(), 0.001);
        
        measurements.setBicepsCircumference(38.0);
        assertEquals(38.0, measurements.getBicepsCircumference(), 0.001);
        
        measurements.setThighCircumference(65.0);
        assertEquals(65.0, measurements.getThighCircumference(), 0.001);
        
        Map<String, Double> additionalMeasures = new HashMap<>();
        additionalMeasures.put("forearm", 30.0);
        measurements.setAdditionalMeasures(additionalMeasures);
        assertEquals(30.0, measurements.getAdditionalMeasures().get("forearm"), 0.001);
    }

    @Test
    void testGetBmi() {
        BodyMeasurements measurements = new BodyMeasurements();
        measurements.setHeight(180.0);
        
        // BMI = weight / (height in meters)²
        // For height = 1.8m, weight = 80kg, BMI should be 80 / (1.8)² = 80 / 3.24 = 24.69
        double bmi = measurements.getBmi(80.0);
        assertEquals(24.69, bmi, 0.01);
    }

    @Test
    void testGetBmiWithZeroHeight() {
        BodyMeasurements measurements = new BodyMeasurements();
        measurements.setHeight(0);
        assertEquals(0, measurements.getBmi(70.0), 0.001);
        
        measurements.setHeight(-10);
        assertEquals(0, measurements.getBmi(70.0), 0.001);
    }

    @Test
    void testGetWaistToHipRatio() {
        BodyMeasurements measurements = new BodyMeasurements();
        measurements.setWaistCircumference(80.0);
        measurements.setHipCircumference(100.0);
        
        // Waist-to-hip ratio = 80 / 100 = 0.8
        assertEquals(0.8, measurements.getWaistToHipRatio(), 0.001);
    }

    @Test
    void testGetWaistToHipRatioWithZeroHipCircumference() {
        BodyMeasurements measurements = new BodyMeasurements();
        measurements.setWaistCircumference(80.0);
        measurements.setHipCircumference(0);
        
        assertEquals(0, measurements.getWaistToHipRatio(), 0.001);
    }

    @Test
    void testHasImprovedFrom() {
        BodyMeasurements previous = new BodyMeasurements();
        previous.setWaistCircumference(90.0);
        
        BodyMeasurements current = new BodyMeasurements();
        current.setWaistCircumference(85.0);
        
        assertTrue(current.hasImprovedFrom(previous));
        
        // Test no improvement
        BodyMeasurements noImprovement = new BodyMeasurements();
        noImprovement.setWaistCircumference(95.0);
        
        assertFalse(noImprovement.hasImprovedFrom(previous));
    }

    @Test
    void testAdditionalMeasures() {
        BodyMeasurements measurements = new BodyMeasurements();
        Map<String, Double> additionalMeasures = new HashMap<>();
        additionalMeasures.put("neck", 40.0);
        additionalMeasures.put("forearm", 30.0);
        
        measurements.setAdditionalMeasures(additionalMeasures);
        
        assertEquals(40.0, measurements.getAdditionalMeasures().get("neck"), 0.001);
        assertEquals(30.0, measurements.getAdditionalMeasures().get("forearm"), 0.001);
        assertEquals(2, measurements.getAdditionalMeasures().size());
    }
}