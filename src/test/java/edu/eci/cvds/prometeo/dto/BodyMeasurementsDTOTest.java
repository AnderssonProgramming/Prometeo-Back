package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;




public class BodyMeasurementsDTOTest {
    
    @Test
    public void testGettersAndSetters() {
        // Create a DTO instance
        BodyMeasurementsDTO dto = new BodyMeasurementsDTO();
        
        // Test height
        dto.setHeight(180.5);
        assertEquals(180.5, dto.getHeight(), 0.001);
        
        // Test chestCircumference
        dto.setChestCircumference(95.2);
        assertEquals(95.2, dto.getChestCircumference(), 0.001);
        
        // Test waistCircumference
        dto.setWaistCircumference(82.7);
        assertEquals(82.7, dto.getWaistCircumference(), 0.001);
        
        // Test hipCircumference
        dto.setHipCircumference(98.3);
        assertEquals(98.3, dto.getHipCircumference(), 0.001);
        
        // Test bicepsCircumference
        dto.setBicepsCircumference(35.1);
        assertEquals(35.1, dto.getBicepsCircumference(), 0.001);
        
        // Test thighCircumference
        dto.setThighCircumference(58.6);
        assertEquals(58.6, dto.getThighCircumference(), 0.001);
        
        // Test additionalMeasures
        Map<String, Double> additionalMeasures = new HashMap<>();
        additionalMeasures.put("neckCircumference", 38.2);
        additionalMeasures.put("calfCircumference", 37.5);
        
        dto.setAdditionalMeasures(additionalMeasures);
        assertEquals(additionalMeasures, dto.getAdditionalMeasures());
        assertEquals(38.2, dto.getAdditionalMeasures().get("neckCircumference"), 0.001);
        assertEquals(37.5, dto.getAdditionalMeasures().get("calfCircumference"), 0.001);
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical DTOs
        BodyMeasurementsDTO dto1 = new BodyMeasurementsDTO();
        dto1.setHeight(175.0);
        dto1.setChestCircumference(90.0);
        dto1.setWaistCircumference(80.0);
        dto1.setHipCircumference(95.0);
        dto1.setBicepsCircumference(32.0);
        dto1.setThighCircumference(55.0);
        
        Map<String, Double> additionalMeasures1 = new HashMap<>();
        additionalMeasures1.put("neckCircumference", 38.0);
        dto1.setAdditionalMeasures(additionalMeasures1);
        
        BodyMeasurementsDTO dto2 = new BodyMeasurementsDTO();
        dto2.setHeight(175.0);
        dto2.setChestCircumference(90.0);
        dto2.setWaistCircumference(80.0);
        dto2.setHipCircumference(95.0);
        dto2.setBicepsCircumference(32.0);
        dto2.setThighCircumference(55.0);
        
        Map<String, Double> additionalMeasures2 = new HashMap<>();
        additionalMeasures2.put("neckCircumference", 38.0);
        dto2.setAdditionalMeasures(additionalMeasures2);
        
        // Test equals
        assertEquals(dto1, dto2);
        
        // Test hashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
        
        // Modify one DTO and test not equals
        dto2.setHeight(180.0);
        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }
    
    @Test
    public void testToString() {
        BodyMeasurementsDTO dto = new BodyMeasurementsDTO();
        dto.setHeight(170.0);
        dto.setChestCircumference(92.0);
        
        String toString = dto.toString();
        
        // Verify the toString contains the field names and values
        assertTrue(toString.contains("height"));
        assertTrue(toString.contains("170.0"));
        assertTrue(toString.contains("chestCircumference"));
        assertTrue(toString.contains("92.0"));
    }
}