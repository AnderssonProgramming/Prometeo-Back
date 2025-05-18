package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.model.enums.ReportFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for generating reports and statistics
 */
public interface ReportService {

    /**
     * Generates a user progress report.
     * This report includes the user's physical progress data such as weight and goal.
     *
     * @param userId ID of the user whose progress data is to be reported.
     * @param format Format in which the report will be generated (e.g., PDF, XLSX, CSV, JSON).
     * @return A byte array containing the generated report data in the requested format.
     */
    byte[] generateUserProgressReport(UUID userId, ReportFormat format);

    /**
     * Generates a gym usage report.
     * This report provides details about gym session usage, such as total capacity, reserved spots, and utilization rate,
     * for a given date range.
     *
     * @param startDate The start date of the period for the report.
     * @param endDate The end date of the period for the report.
     * @param format Format in which the report will be generated (e.g., PDF, XLSX, CSV, JSON).
     * @return A byte array containing the generated gym usage report in the requested format.
     */
    byte[] generateGymUsageReport(LocalDate startDate, LocalDate endDate, ReportFormat format);

    /**
     * Gets attendance statistics for gym sessions within a specific date range.
     * This includes data such as the number of attendees for each session.
     *
     * @param startDate The start date of the period for the statistics.
     * @param endDate The end date of the period for the statistics.
     * @param format Format in which the statistics will be generated (e.g., PDF, XLSX, CSV, JSON).
     * @return A byte array containing the attendance statistics in the requested format.
     */
    byte[] getAttendanceStatistics(LocalDate startDate, LocalDate endDate, ReportFormat format);
}