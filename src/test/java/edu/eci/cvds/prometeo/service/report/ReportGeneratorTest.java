package edu.eci.cvds.prometeo.service.report;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;






public class ReportGeneratorTest {

    private ReportGenerator reportGenerator;
    private List<TestData> testDataList;
    private List<String> headers;
    private Function<TestData, List<String>> rowMapper;
    private Function<TestData, String> lineMapper;

    @BeforeEach
    void setUp() {
        reportGenerator = new ReportGenerator();
        testDataList = Arrays.asList(
                new TestData(1, "Item 1", 100.0),
                new TestData(2, "Item 2", 200.0),
                new TestData(3, "Item 3", 300.0)
        );
        headers = Arrays.asList("ID", "Name", "Value");
        rowMapper = data -> Arrays.asList(
                String.valueOf(data.getId()),
                data.getName(),
                String.valueOf(data.getValue())
        );
        lineMapper = data -> String.format("ID: %d, Name: %s, Value: %.2f", 
                data.getId(), data.getName(), data.getValue());
    }

    @Test
    void testGenerateJSON() throws IOException {
        // Given testDataList from setUp

        // When
        byte[] result = reportGenerator.generateJSON(testDataList);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Verify content by deserializing
        ObjectMapper mapper = new ObjectMapper();
        List<TestData> deserializedData = mapper.readValue(result, new TypeReference<List<TestData>>() {});
        assertEquals(testDataList.size(), deserializedData.size());
        for (int i = 0; i < testDataList.size(); i++) {
            assertEquals(testDataList.get(i).getId(), deserializedData.get(i).getId());
            assertEquals(testDataList.get(i).getName(), deserializedData.get(i).getName());
            assertEquals(testDataList.get(i).getValue(), deserializedData.get(i).getValue(), 0.001);
        }
    }

    @Test
    void testGenerateCSV() {
        // Given testDataList, headers, and rowMapper from setUp

        // When
        byte[] result = reportGenerator.generateCSV(testDataList, headers, rowMapper);

        // Then
        assertNotNull(result);
        String csvContent = new String(result, StandardCharsets.UTF_8);
        
        // Verify header row
        assertTrue(csvContent.startsWith("ID,Name,Value"));
        
        // Verify data rows
        assertTrue(csvContent.contains("1,Item 1,100.0"));
        assertTrue(csvContent.contains("2,Item 2,200.0"));
        assertTrue(csvContent.contains("3,Item 3,300.0"));
    }

    @Test
    void testGenerateXLSX() throws IOException {
        // Given testDataList, headers, and rowMapper from setUp

        // When
        byte[] result = reportGenerator.generateXLSX(testDataList, headers, rowMapper);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        // Verify Excel content
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(result))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertEquals("Reporte", sheet.getSheetName());
            
            // Check header row
            Row headerRow = sheet.getRow(0);
            assertEquals("ID", headerRow.getCell(0).getStringCellValue());
            assertEquals("Name", headerRow.getCell(1).getStringCellValue());
            assertEquals("Value", headerRow.getCell(2).getStringCellValue());
            
            // Check data rows
            Row firstRow = sheet.getRow(1);
            assertEquals("1", firstRow.getCell(0).getStringCellValue());
            assertEquals("Item 1", firstRow.getCell(1).getStringCellValue());
            assertEquals("100.0", firstRow.getCell(2).getStringCellValue());
        }
    }

    @Test
    void testGeneratePDF() throws IOException {
        // Given testDataList and lineMapper from setUp

        // When
        byte[] result = reportGenerator.generatePDF(testDataList, "Test Report", lineMapper);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        
        // Verify PDF is valid by loading it
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(result))) {
            assertNotNull(document);
            assertTrue(document.getNumberOfPages() > 0);
        }
    }

    @Test
    void testEmptyList() throws IOException {
        // Given
        List<TestData> emptyList = Collections.emptyList();
        
        // When
        byte[] jsonResult = reportGenerator.generateJSON(emptyList);
        byte[] csvResult = reportGenerator.generateCSV(emptyList, headers, rowMapper);
        byte[] xlsxResult = reportGenerator.generateXLSX(emptyList, headers, rowMapper);
        byte[] pdfResult = reportGenerator.generatePDF(emptyList, "Empty Report", lineMapper);
        
        // Then
        assertEquals("[]", new String(jsonResult, StandardCharsets.UTF_8));
        assertTrue(new String(csvResult, StandardCharsets.UTF_8).contains("ID,Name,Value"));
        assertTrue(xlsxResult.length > 0);
        assertTrue(pdfResult.length > 0);
    }

    // Simple test data class
    static class TestData {
        private int id;
        private String name;
        private double value;

        public TestData() {
            // Default constructor for Jackson
        }

        public TestData(int id, String name, double value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}