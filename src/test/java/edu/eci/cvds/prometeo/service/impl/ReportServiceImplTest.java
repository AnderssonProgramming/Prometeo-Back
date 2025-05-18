package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.PhysicalProgress;
import edu.eci.cvds.prometeo.model.Weight;
import edu.eci.cvds.prometeo.model.enums.ReportFormat;
import edu.eci.cvds.prometeo.repository.GymSessionRepository;
import edu.eci.cvds.prometeo.repository.PhysicalProgressRepository;
import edu.eci.cvds.prometeo.service.report.ReportGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;






@ExtendWith(MockitoExtension.class)
public class ReportServiceImplTest {

    @Mock
    private PhysicalProgressRepository physicalProgressRepository;

    @Mock
    private GymSessionRepository gymSessionRepository;

    @Mock
    private ReportGenerator reportGeneratorMock;

    @InjectMocks
    private ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        // Set the mocked ReportGenerator to the reportService
        ReflectionTestUtils.setField(reportService, "reportGenerator", reportGeneratorMock);
    }

    @Test
    void testGenerateUserProgressReport() throws IOException {
        // Arrange
        UUID userId = UUID.randomUUID();
        ReportFormat format = ReportFormat.PDF;
        
        PhysicalProgress progress1 = createTestProgress(userId, LocalDate.of(2023, 6, 1), 75.0, "Lose weight");
        PhysicalProgress progress2 = createTestProgress(userId, LocalDate.of(2023, 6, 15), 73.5, "Gain muscle");
        List<PhysicalProgress> progressList = Arrays.asList(progress1, progress2);
        
        when(physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId)).thenReturn(progressList);
        
        byte[] expectedReport = "PDF report content".getBytes();
        when(reportGeneratorMock.generatePDF(eq(progressList), anyString(), any())).thenReturn(expectedReport);
        
        // Act
        byte[] result = reportService.generateUserProgressReport(userId, format);
        
        // Assert
        assertArrayEquals(expectedReport, result);
        verify(physicalProgressRepository).findByUserIdOrderByRecordDateDesc(userId);
        verify(reportGeneratorMock).generatePDF(eq(progressList), anyString(), any());
    }

    @Test
    void testGenerateUserProgressReportWithXLSXFormat() throws IOException {
        // Arrange
        UUID userId = UUID.randomUUID();
        ReportFormat format = ReportFormat.XLSX;
        
        List<PhysicalProgress> progressList = Collections.emptyList();
        when(physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId)).thenReturn(progressList);
        
        byte[] expectedReport = "XLSX report content".getBytes();
        when(reportGeneratorMock.generateXLSX(eq(progressList), anyList(), any())).thenReturn(expectedReport);
        
        // Act
        byte[] result = reportService.generateUserProgressReport(userId, format);
        
        // Assert
        assertArrayEquals(expectedReport, result);
        verify(physicalProgressRepository).findByUserIdOrderByRecordDateDesc(userId);
        verify(reportGeneratorMock).generateXLSX(eq(progressList), anyList(), any());
    }

    @Test
    void testGenerateUserProgressReportThrowsException() throws IOException {
        // Arrange
        UUID userId = UUID.randomUUID();
        ReportFormat format = ReportFormat.PDF;
        
        List<PhysicalProgress> progressList = Collections.emptyList();
        when(physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId)).thenReturn(progressList);
        
        IOException ioException = new IOException("Test exception");
        when(reportGeneratorMock.generatePDF(eq(progressList), anyString(), any())).thenThrow(ioException);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reportService.generateUserProgressReport(userId, format));
        
        assertEquals("Error generando reporte en formato: " + format, exception.getMessage());
        assertEquals(ioException, exception.getCause());
    }

    @Test
    void testGenerateGymUsageReport() throws IOException {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 6, 30);
        ReportFormat format = ReportFormat.PDF;
        
        GymSession session1 = createTestSession(LocalDate.of(2023, 6, 10), 20, 15, 60);
        GymSession session2 = createTestSession(LocalDate.of(2023, 6, 20), 25, 20, 90);
        List<GymSession> sessionList = Arrays.asList(session1, session2);
        
        when(gymSessionRepository.findBySessionDateBetween(startDate, endDate)).thenReturn(sessionList);
        
        byte[] expectedReport = "PDF report content".getBytes();
        when(reportGeneratorMock.generatePDF(eq(sessionList), anyString(), any())).thenReturn(expectedReport);
        
        // Act
        byte[] result = reportService.generateGymUsageReport(startDate, endDate, format);
        
        // Assert
        assertArrayEquals(expectedReport, result);
        verify(gymSessionRepository).findBySessionDateBetween(startDate, endDate);
        verify(reportGeneratorMock).generatePDF(eq(sessionList), anyString(), any());
    }

    @Test
    void testGenerateGymUsageReportWithJSONFormat() throws IOException {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 6, 30);
        ReportFormat format = ReportFormat.JSON;
        
        List<GymSession> sessionList = Collections.emptyList();
        when(gymSessionRepository.findBySessionDateBetween(startDate, endDate)).thenReturn(sessionList);
        
        byte[] expectedReport = "JSON report content".getBytes();
        when(reportGeneratorMock.generateJSON(anyList())).thenReturn(expectedReport);
        
        // Act
        byte[] result = reportService.generateGymUsageReport(startDate, endDate, format);
        
        // Assert
        assertArrayEquals(expectedReport, result);
        verify(gymSessionRepository).findBySessionDateBetween(startDate, endDate);
        verify(reportGeneratorMock).generateJSON(anyList());
    }

    @Test
    void testGetAttendanceStatistics() throws IOException {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 6, 30);
        ReportFormat format = ReportFormat.PDF;
        
        GymSession session1 = createTestSession(LocalDate.of(2023, 6, 10), 20, 15, 60);
        GymSession session2 = createTestSession(LocalDate.of(2023, 6, 20), 25, 20, 90);
        List<GymSession> sessionList = Arrays.asList(session1, session2);
        
        when(gymSessionRepository.findBySessionDateBetween(startDate, endDate)).thenReturn(sessionList);
        
        byte[] expectedReport = "PDF report content".getBytes();
        when(reportGeneratorMock.generatePDF(anyList(), anyString(), any())).thenReturn(expectedReport);
        
        // Act
        byte[] result = reportService.getAttendanceStatistics(startDate, endDate, format);
        
        // Assert
        assertArrayEquals(expectedReport, result);
        verify(gymSessionRepository).findBySessionDateBetween(startDate, endDate);
        verify(reportGeneratorMock).generatePDF(anyList(), anyString(), any());
    }

    @Test
    void testGetAttendanceStatisticsWithCSVFormat() throws IOException {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 6, 30);
        ReportFormat format = ReportFormat.CSV;
        
        List<GymSession> sessionList = Collections.emptyList();
        when(gymSessionRepository.findBySessionDateBetween(startDate, endDate)).thenReturn(sessionList);
        
        byte[] expectedReport = "CSV report content".getBytes();
        when(reportGeneratorMock.generateCSV(anyList(), anyList(), any())).thenReturn(expectedReport);
        
        // Act
        byte[] result = reportService.getAttendanceStatistics(startDate, endDate, format);
        
        // Assert
        assertArrayEquals(expectedReport, result);
        verify(gymSessionRepository).findBySessionDateBetween(startDate, endDate);
        verify(reportGeneratorMock).generateCSV(anyList(), anyList(), any());
    }

    @Test
    void testGetAttendanceStatisticsThrowsException() throws IOException {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 6, 30);
        ReportFormat format = ReportFormat.PDF;
        
        List<GymSession> sessionList = Collections.emptyList();
        when(gymSessionRepository.findBySessionDateBetween(startDate, endDate)).thenReturn(sessionList);
        
        IOException ioException = new IOException("Test exception");
        when(reportGeneratorMock.generatePDF(anyList(), anyString(), any())).thenThrow(ioException);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reportService.getAttendanceStatistics(startDate, endDate, format));
        
        assertEquals("Error generando reporte en formato: " + format, exception.getMessage());
        assertEquals(ioException, exception.getCause());
    }    @Test
    void testMapLine() throws Exception {
        // Arrange
        GymSession session = createTestSession(LocalDate.of(2023, 6, 10), 20, 15, 60);
        String expectedLine = String.format(
            "Fecha: %s | Capacidad Total: %d | Reservas Totales: %d | Tasa de Utilización: %.2f%% | Utilización Promedio: %.2f%% | Duración Promedio: %d minutos",
            session.getSessionDate(), session.getCapacity(), session.getReservedSpots(),
            session.getReservedSpots() * 100.0 / session.getCapacity(),
            session.getReservedSpots() * 100.0 / session.getCapacity(),
            session.getDuration().toMinutes()
        );
        
        // Invoke private method through reflection
        java.lang.reflect.Method mapLineMethod = ReportServiceImpl.class.getDeclaredMethod(
            "mapLine", GymSession.class);
        mapLineMethod.setAccessible(true);
        
        // Act
        String result = (String) mapLineMethod.invoke(reportService, session);
        
        // Assert
        assertEquals(expectedLine, result);
    }
    
    @Test
    void testUserProgressLineMapper() throws Exception {
        // Arrange
        PhysicalProgress progress = createTestProgress(UUID.randomUUID(), 
            LocalDate.of(2023, 6, 1), 75.0, "Lose weight");
        
        String expectedLine = "Fecha: " + progress.getRecordDate() +
            " | Peso: " + progress.getWeight().getValue() + "kg" +
            " | Meta: " + progress.getPhysicalGoal();
            
        // Get the lineMapper function directly from the implementation
        // This tests the lambda$generateUserProgressReport$0 method
        java.lang.reflect.Method generateUserProgressReportMethod = ReportServiceImpl.class.getDeclaredMethod(
            "generateUserProgressReport", UUID.class, ReportFormat.class);
        generateUserProgressReportMethod.setAccessible(true);
        
        // We need to extract the function from the implementation
        // Since we can't directly access lambdas, we'll test the behavior by using the ReportGenerator's call pattern
        
        // Mock the generatePDF to capture the lineMapper function
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            java.util.function.Function<PhysicalProgress, String> lineMapper = 
                (java.util.function.Function<PhysicalProgress, String>) invocation.getArgument(2);
            
            // Apply the lineMapper to our test progress
            String result = lineMapper.apply(progress);
            
            // Assert within the mock
            assertEquals(expectedLine, result);
            
            return "test".getBytes();
        }).when(reportGeneratorMock).generatePDF(anyList(), anyString(), any());
        
        // Act - trigger the use of the lineMapper
        reportService.generateUserProgressReport(UUID.randomUUID(), ReportFormat.PDF);
    }
    
    @Test
    void testUserProgressRowMapper() throws Exception {
        // Arrange
        PhysicalProgress progress = createTestProgress(UUID.randomUUID(), 
            LocalDate.of(2023, 6, 1), 75.0, "Lose weight");
            
        List<String> expectedRow = List.of(
            progress.getRecordDate().toString(),
            String.valueOf(progress.getWeight().getValue()),
            progress.getPhysicalGoal()
        );
        
        // Mock the generateXLSX to capture the rowMapper function
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            java.util.function.Function<PhysicalProgress, List<String>> rowMapper = 
                (java.util.function.Function<PhysicalProgress, List<String>>) invocation.getArgument(2);
            
            // Apply the rowMapper to our test progress
            List<String> result = rowMapper.apply(progress);
            
            // Assert within the mock
            assertEquals(expectedRow, result);
            
            return "test".getBytes();
        }).when(reportGeneratorMock).generateXLSX(anyList(), anyList(), any());
        
        // Act - trigger the use of the rowMapper
        reportService.generateUserProgressReport(UUID.randomUUID(), ReportFormat.XLSX);
    }
    
    @Test
    void testAttendanceStatisticsLineMapper() throws IOException {
        // Arrange
        LocalDate date = LocalDate.of(2023, 6, 1);
        Integer attendanceCount = 42;
        Map.Entry<LocalDate, Integer> entry = Map.entry(date, attendanceCount);
        
        String expectedLine = "Fecha: " + date + " | Asistencias: " + attendanceCount;
        
        // Mock the generatePDF to capture the lineMapper function
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            java.util.function.Function<Map.Entry<LocalDate, Integer>, String> lineMapper = 
                (java.util.function.Function<Map.Entry<LocalDate, Integer>, String>) invocation.getArgument(2);
            
            // Apply the lineMapper to our test entry
            String result = lineMapper.apply(entry);
            
            // Assert within the mock
            assertEquals(expectedLine, result);
            
            return "test".getBytes();
        }).when(reportGeneratorMock).generatePDF(anyList(), anyString(), any());
        
        // Act - trigger the use of the lineMapper
        reportService.getAttendanceStatistics(LocalDate.now(), LocalDate.now(), ReportFormat.PDF);
    }
    
    @Test
    void testAttendanceStatisticsRowMapper() throws IOException {
        // Arrange
        LocalDate date = LocalDate.of(2023, 6, 1);
        Integer attendanceCount = 42;
        Map.Entry<LocalDate, Integer> entry = Map.entry(date, attendanceCount);
        
        List<String> expectedRow = List.of(
            date.toString(),
            String.valueOf(attendanceCount)
        );
        
        // Mock the generateXLSX to capture the rowMapper function
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            java.util.function.Function<Map.Entry<LocalDate, Integer>, List<String>> rowMapper = 
                (java.util.function.Function<Map.Entry<LocalDate, Integer>, List<String>>) invocation.getArgument(2);
            
            // Apply the rowMapper to our test entry
            List<String> result = rowMapper.apply(entry);
            
            // Assert within the mock
            assertEquals(expectedRow, result);
            
            return "test".getBytes();
        }).when(reportGeneratorMock).generateXLSX(anyList(), anyList(), any());
        
        // Act - trigger the use of the rowMapper
        reportService.getAttendanceStatistics(LocalDate.now(), LocalDate.now(), ReportFormat.XLSX);
    }

    // Helper methods to create test data
    private PhysicalProgress createTestProgress(UUID userId, LocalDate date, double weightValue, String goal) {
        PhysicalProgress progress = new PhysicalProgress();
        progress.setUserId(userId);
        progress.setRecordDate(date);
        
        Weight weight = new Weight();
        weight.setValue(weightValue);
        progress.setWeight(weight);
        progress.setPhysicalGoal(goal);
        
        return progress;
    }
      private GymSession createTestSession(LocalDate date, int capacity, int reservedSpots, int durationMinutes) {
        GymSession session = new GymSession();
        session.setSessionDate(date);
        session.setCapacity(capacity);
        session.setReservedSpots(reservedSpots);
        // Set start and end times so that getDuration() works properly
        session.setStartTime(LocalTime.of(10, 0)); // 10:00 AM
        session.setEndTime(LocalTime.of(10, 0).plusMinutes(durationMinutes)); // End time based on duration
        return session;
    }
}