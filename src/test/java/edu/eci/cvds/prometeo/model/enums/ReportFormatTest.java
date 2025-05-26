package edu.eci.cvds.prometeo.model.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ReportFormatTest {

    @Test
    public void testEnumValues() {
        // Test that the enum has the expected number of values
        assertEquals(4, ReportFormat.values().length);
        
        // Test that each expected value exists
        assertNotNull(ReportFormat.CSV);
        assertNotNull(ReportFormat.PDF);
        assertNotNull(ReportFormat.XLSX);
        assertNotNull(ReportFormat.JSON);
    }

    @Test
    public void testValueOf() {
        // Test that valueOf returns the correct enum value for each expected string
        assertEquals(ReportFormat.CSV, ReportFormat.valueOf("CSV"));
        assertEquals(ReportFormat.PDF, ReportFormat.valueOf("PDF"));
        assertEquals(ReportFormat.XLSX, ReportFormat.valueOf("XLSX"));
        assertEquals(ReportFormat.JSON, ReportFormat.valueOf("JSON"));
    }


    @Test
    public void testEnumValuesContent() {
        // Test that values() returns all expected values
        ReportFormat[] formats = ReportFormat.values();
        
        assertEquals(ReportFormat.CSV, formats[0]);
        assertEquals(ReportFormat.PDF, formats[1]);
        assertEquals(ReportFormat.XLSX, formats[2]);
        assertEquals(ReportFormat.JSON, formats[3]);
    }
}