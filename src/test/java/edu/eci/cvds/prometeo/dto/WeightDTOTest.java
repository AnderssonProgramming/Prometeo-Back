package edu.eci.cvds.prometeo.dto;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;




public class WeightDTOTest {

    @Test
    public void testGetAndSetValue() {
        // Arrange
        WeightDTO weight = new WeightDTO();
        double expectedValue = 75.5;
        
        // Act
        weight.setValue(expectedValue);
        double actualValue = weight.getValue();
        
        // Assert
        assertEquals(expectedValue, actualValue, 0.001);
    }
    
    @Test
    public void testGetAndSetUnit() {
        // Arrange
        WeightDTO weight = new WeightDTO();
        String expectedUnit = "KG";
        
        // Act
        weight.setUnit(expectedUnit);
        String actualUnit = weight.getUnit();
        
        // Assert
        assertEquals(expectedUnit, actualUnit);
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        WeightDTO weight1 = new WeightDTO();
        weight1.setValue(80.0);
        weight1.setUnit("LB");
        
        WeightDTO weight2 = new WeightDTO();
        weight2.setValue(80.0);
        weight2.setUnit("LB");
        
        WeightDTO differentWeight = new WeightDTO();
        differentWeight.setValue(70.0);
        differentWeight.setUnit("KG");
        
        // Assert
        assertEquals(weight1, weight2);
        assertEquals(weight1.hashCode(), weight2.hashCode());
        assertNotEquals(weight1, differentWeight);
        assertNotEquals(weight1.hashCode(), differentWeight.hashCode());
    }
    
    @Test
    public void testToString() {
        // Arrange
        WeightDTO weight = new WeightDTO();
        weight.setValue(65.5);
        weight.setUnit("KG");
        
        // Act
        String toString = weight.toString();
        
        // Assert
        assertTrue(toString.contains("65.5"));
        assertTrue(toString.contains("KG"));
    }
}