package edu.eci.cvds.prometeo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



public class WeightTest {

    private static final double DELTA = 0.001; // Delta for double comparison

    @Test
    public void testConstructor() {
        Weight weight = new Weight(75.5, Weight.WeightUnit.KG);
        assertEquals(75.5, weight.getValue(), DELTA);
        assertEquals(Weight.WeightUnit.KG, weight.getUnit());
    }

    @Test
    public void testDefaultConstructor() {
        Weight weight = new Weight();
        assertEquals(0.0, weight.getValue(), DELTA);
        assertNull(weight.getUnit());
    }

    @Test
    public void testGettersAndSetters() {
        Weight weight = new Weight();
        weight.setValue(65.3);
        weight.setUnit(Weight.WeightUnit.LB);
        
        assertEquals(65.3, weight.getValue(), DELTA);
        assertEquals(Weight.WeightUnit.LB, weight.getUnit());
    }

    @Test
    public void testConvertKgToLb() {
        Weight weight = new Weight(50.0, Weight.WeightUnit.KG);
        double lbValue = weight.convertTo(Weight.WeightUnit.LB);
        assertEquals(110.231, lbValue, DELTA);
    }

    @Test
    public void testConvertLbToKg() {
        Weight weight = new Weight(100.0, Weight.WeightUnit.LB);
        double kgValue = weight.convertTo(Weight.WeightUnit.KG);
        assertEquals(45.359, kgValue, DELTA);
    }

    @Test
    public void testConvertToSameUnit() {
        Weight weight = new Weight(75.0, Weight.WeightUnit.KG);
        double kgValue = weight.convertTo(Weight.WeightUnit.KG);
        assertEquals(75.0, kgValue, DELTA);

        Weight weight2 = new Weight(165.0, Weight.WeightUnit.LB);
        double lbValue = weight2.convertTo(Weight.WeightUnit.LB);
        assertEquals(165.0, lbValue, DELTA);
    }

    @Test
    public void testZeroWeight() {
        Weight weight = new Weight(0.0, Weight.WeightUnit.KG);
        assertEquals(0.0, weight.convertTo(Weight.WeightUnit.LB), DELTA);
        
        Weight weight2 = new Weight(0.0, Weight.WeightUnit.LB);
        assertEquals(0.0, weight2.convertTo(Weight.WeightUnit.KG), DELTA);
    }

    @Test
    public void testNegativeWeight() {
        Weight weight = new Weight(-10.0, Weight.WeightUnit.KG);
        assertEquals(-22.0462, weight.convertTo(Weight.WeightUnit.LB), DELTA);
        
        Weight weight2 = new Weight(-22.0462, Weight.WeightUnit.LB);
        assertEquals(-10.0, weight2.convertTo(Weight.WeightUnit.KG), DELTA);
    }

    @Test
    public void testKnownConversions() {
        // Test some known weight conversions
        Weight oneKg = new Weight(1.0, Weight.WeightUnit.KG);
        assertEquals(2.20462, oneKg.convertTo(Weight.WeightUnit.LB), DELTA);
        
        Weight oneLb = new Weight(1.0, Weight.WeightUnit.LB);
        assertEquals(0.45359, oneLb.convertTo(Weight.WeightUnit.KG), DELTA);
    }
}