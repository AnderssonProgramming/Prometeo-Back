package edu.eci.cvds.prometeo.service.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.cvds.prometeo.model.PhysicalProgress;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * ReportGenerator is a utility component for generating reports in different formats such as
 * JSON, CSV, XLSX (Excel), and PDF. It provides generic methods to serialize and format data,
 * allowing reuse across various types of entities and data models.
 */
@Component
public class ReportGenerator {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generates a JSON report from a list of data objects.
     *
     * @param data The list of data to serialize.
     * @param <T>  The type of the objects in the list.
     * @return A byte array representing the JSON content.
     * @throws IOException If serialization fails.
     */
    public <T> byte[] generateJSON(List<T> data) throws IOException {
        return objectMapper.writeValueAsBytes(data);
    }

    /**
     * Generates a CSV report from a list of data objects.
     *
     * @param data      The list of data to serialize.
     * @param headers   The list of headers to include as the first row.
     * @param rowMapper A function that maps each object to a list of string values.
     * @param <T>       The type of the objects in the list.
     * @return A byte array representing the CSV content.
     */
    public <T> byte[] generateCSV(List<T> data, List<String> headers, Function<T, List<String>> rowMapper) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(",", headers)).append("\n");
        for (T item : data) {
            builder.append(String.join(",", rowMapper.apply(item))).append("\n");
        }
        return builder.toString().getBytes();
    }

    /**
     * Generates an Excel (XLSX) report from a list of data objects.
     *
     * @param data      The list of data to include.
     * @param headers   The column headers.
     * @param rowMapper A function that maps each object to a list of string values for each column.
     * @param <T>       The type of the objects in the list.
     * @return A byte array representing the Excel file.
     * @throws IOException If an error occurs during file writing.
     */
    public <T> byte[] generateXLSX(List<T> data, List<String> headers, Function<T, List<String>> rowMapper) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte");
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            int rowIdx = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowIdx++);
                List<String> values = rowMapper.apply(item);
                for (int i = 0; i < values.size(); i++) {
                    row.createCell(i).setCellValue(values.get(i));
                }
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    /**
     * Generates a PDF report from a list of data objects.
     *
     * @param data       The list of data to include in the report.
     * @param title      The title of the PDF document.
     * @param lineMapper A function that maps each object to a string to be rendered as a line in the PDF.
     * @param <T>        The type of the objects in the list.
     * @return A byte array representing the PDF content.
     * @throws IOException If an error occurs during PDF generation.
     */
    public <T> byte[] generatePDF(List<T> data, String title, Function<T, String> lineMapper) throws IOException {
        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.newLineAtOffset(50, 700);
            content.showText(title);
            content.endText();

            int y = 680;
            for (T item : data) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 10);
                content.newLineAtOffset(50, y);
                content.showText(lineMapper.apply(item));
                content.endText();
                y -= 15;
                if (y < 50) {
                    content.close();
                    page = new PDPage(PDRectangle.LETTER);
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    y = 700;
                }
            }

            content.close();
            doc.save(out);
            return out.toByteArray();
        }
    }
}
