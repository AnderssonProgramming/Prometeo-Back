package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.GymSession;
import edu.eci.cvds.prometeo.model.PhysicalProgress;
import edu.eci.cvds.prometeo.repository.*;
import edu.eci.cvds.prometeo.service.ReportService;
import edu.eci.cvds.prometeo.model.enums.ReportFormat;

import edu.eci.cvds.prometeo.service.report.ReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

/**
 * Implementation of the ReportService interface.
 * This service generates various reports including user progress, gym usage, and attendance statistics.
 */
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private PhysicalProgressRepository physicalProgressRepository;
    @Autowired
    private GymSessionRepository gymSessionRepository;

    private final ReportGenerator reportGenerator = new ReportGenerator();

    /**
     * Generates a report on user progress (weight and goal data).
     *
     * @param userId ID of the user whose progress data is to be reported.
     * @param format The desired format for the report (e.g., PDF, XLSX, CSV, JSON).
     * @return A byte array containing the report data in the requested format.
     */
    @Override
    public byte[] generateUserProgressReport(UUID userId, ReportFormat format) {
        List<PhysicalProgress> data = physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId);

        List<String> headers = List.of("Fecha", "Peso", "Meta");
        Function<PhysicalProgress, List<String>> rowMapper = p -> List.of(
              p.getRecordDate().toString(),
              p.getWeight() != null ? String.valueOf(p.getWeight().getValue()) : "N/A",
              p.getPhysicalGoal() != null ? p.getPhysicalGoal() : "N/A"
        );

        Function<PhysicalProgress, String> lineMapper = p ->
              "Fecha: " + p.getRecordDate() +
              " | Peso: " + (p.getWeight() != null ? p.getWeight().getValue() + "kg" : "N/A") +
              " | Meta: " + (p.getPhysicalGoal() != null ? p.getPhysicalGoal() : "N/A");

        try {
          return switch (format) {
              case PDF -> reportGenerator.generatePDF(data, "Reporte de Progreso Físico", lineMapper);
              case XLSX -> reportGenerator.generateXLSX(data, headers, rowMapper);
              case CSV -> reportGenerator.generateCSV(data, headers, rowMapper);
              case JSON -> reportGenerator.generateJSON(data);
          };
        } catch (IOException e) {
          throw new RuntimeException("Error generando reporte en formato: " + format, e);
        }
    }

    /**
     * Generates a gym usage report.
     *
     * @param startDate The start date of the period for the gym usage report.
     * @param endDate The end date of the period for the gym usage report.
     * @param format The desired format for the report (e.g., PDF, XLSX, CSV, JSON).
     * @return A byte array containing the gym usage report data in the requested format.
     */
    @Override
    public byte[] generateGymUsageReport(LocalDate startDate, LocalDate endDate, ReportFormat format) {
        List<GymSession> sessions = gymSessionRepository.findBySessionDateBetween(startDate, endDate);

        Map<String, Object> metrics = generateMetrics(sessions, startDate, endDate);
        List<Map<String, Object>> reportData = List.of(metrics);
        List<String> headers = List.of("Fecha", "Capacidad Total", "Reservas Totales", "Tasa de Utilización", "Utilización Promedio", "Duración Promedio");

        Function<GymSession, List<String>> rowMapper = this::mapRow;
        Function<GymSession, String> lineMapper = this::mapLine;

        try {
            return switch (format) {
                case PDF -> reportGenerator.generatePDF(sessions, "Reporte de Uso del Gimnasio", lineMapper);
                case XLSX -> reportGenerator.generateXLSX(sessions, headers, rowMapper);
                case CSV -> reportGenerator.generateCSV(sessions, headers, rowMapper);
                case JSON -> reportGenerator.generateJSON(reportData);
            };
        } catch (IOException e) {
            throw new RuntimeException("Error generando reporte en formato: " + format, e);
        }
    }

    /*
     * Generates metrics for the gym usage report.
     *
     * @param sessions List of gym sessions to generate metrics from.
     * @param startDate The start date of the period for the metrics.
     * @param endDate The end date of the period for the metrics.
     * @return A map containing key metrics (total sessions, total capacity, total reserved spots, etc.).
     */
    private Map<String, Object> generateMetrics(List<GymSession> sessions, LocalDate startDate, LocalDate endDate) {
        long totalSessions = sessions.size();
        int totalCapacity = sessions.stream().mapToInt(GymSession::getCapacity).sum();
        int totalReserved = sessions.stream().mapToInt(GymSession::getReservedSpots).sum();
        double utilizationRate = totalCapacity > 0 ? (totalReserved * 100.0 / totalCapacity) : 0;
        double avgUtilization = sessions.isEmpty() ? 0.0 : sessions.stream()
                .mapToDouble(s -> s.getReservedSpots() * 100.0 / s.getCapacity())
                .average().orElse(0.0);
        double avgDuration = sessions.isEmpty() ? 0.0 : sessions.stream()
                .mapToLong(s -> s.getDuration().toMinutes())
                .average().orElse(0.0);

        return Map.of(
                "startDate", startDate.toString(),
                "endDate", endDate.toString(),
                "totalSessions", totalSessions,
                "totalCapacity", totalCapacity,
                "totalReservedSpots", totalReserved,
                "utilizationRate", String.format("%.2f", utilizationRate) + "%",
                "averageUtilizationPerSession", String.format("%.2f", avgUtilization) + "%",
                "averageSessionDurationMinutes", String.format("%.2f", avgDuration)
        );
    }

    /*
     * Maps a gym session to a row of data for the report.
     *
     * @param session The gym session to map.
     * @return A list of strings representing the session data for the report.
     */
    private List<String> mapRow(GymSession session) {
        return List.of(
                session.getSessionDate().toString(),
                String.valueOf(session.getCapacity()),
                String.valueOf(session.getReservedSpots()),
                String.format("%.2f", session.getReservedSpots() * 100.0 / session.getCapacity()) + "%",
                String.format("%.2f", session.getReservedSpots() * 100.0 / session.getCapacity()),
                String.format("%.2f", session.getDuration().toMinutes())
        );
    }

    /*
     * Maps a gym session to a line of data for the report.
     *
     * @param session The gym session to map.
     * @return A string representing the session data for the report.
     */
private String mapLine(GymSession session) {
    return String.format(
            "Fecha: %s | Capacidad Total: %d | Reservas Totales: %d | Tasa de Utilización: %.2f%% | Utilización Promedio: %.2f%% | Duración Promedio: %d minutos",
            session.getSessionDate(), session.getCapacity(), session.getReservedSpots(),
            session.getReservedSpots() * 100.0 / session.getCapacity(),
            session.getReservedSpots() * 100.0 / session.getCapacity(),
            session.getDuration().toMinutes()
    );
}
    

    /**
     * Generates attendance statistics for the gym sessions within a given date range.
     *
     * @param startDate The start date of the period for the attendance statistics.
     * @param endDate The end date of the period for the attendance statistics.
     * @param format The desired format for the statistics report (e.g., PDF, XLSX, CSV, JSON).
     * @return A byte array containing the attendance statistics in the requested format.
     */
    @Override
    public byte[] getAttendanceStatistics(LocalDate startDate, LocalDate endDate, ReportFormat format) {
        List<GymSession> sessions = gymSessionRepository.findBySessionDateBetween(startDate, endDate);
        Map<LocalDate, Integer> attendanceStats = new HashMap<>();
        for (GymSession session : sessions) {
            attendanceStats.put(session.getSessionDate(), session.getReservedSpots());
        }
        List<String> headers = List.of("Fecha", "Asistencias");
        Function<Map.Entry<LocalDate, Integer>, List<String>> rowMapper = entry -> List.of(
                entry.getKey().toString(),
                String.valueOf(entry.getValue())
        );

        Function<Map.Entry<LocalDate, Integer>, String> lineMapper = entry ->
                "Fecha: " + entry.getKey() + " | Asistencias: " + entry.getValue();

        try {
            return switch (format) {
                case PDF ->
                        reportGenerator.generatePDF(attendanceStats.entrySet().stream().toList(), "Reporte de Asistencia al Gimnasio", lineMapper);
                case XLSX ->
                        reportGenerator.generateXLSX(attendanceStats.entrySet().stream().toList(), headers, rowMapper);
                case CSV ->
                        reportGenerator.generateCSV(attendanceStats.entrySet().stream().toList(), headers, rowMapper);
                case JSON -> reportGenerator.generateJSON(Collections.singletonList(attendanceStats));
            };
        } catch (IOException e) {
            throw new RuntimeException("Error generando reporte en formato: " + format, e);
        }
    }
}