package edu.eci.cvds.prometeo.service;

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
     * Generates a user progress report
     * @param userId ID of the user
     * @param startDate Start date
     * @param endDate End date
     * @param format Format of the report
     * @return Report data as a JSON-compatible map
     */
    // Map<String, Object> generateUserProgressReport(UUID userId, LocalDate startDate, LocalDate endDate, String format);

    /**
     * Generates a gym usage report
     * @param startDate Start date
     * @param endDate End date
     * @param groupBy How to group data (day, week, month)
     * @param format Format of the report
     * @return List of JSON-compatible maps with usage data
     */
    List<Map<String, Object>> generateGymUsageReport(LocalDate startDate, LocalDate endDate, String groupBy, String format);

    /**
     * Generates a trainer performance report
     * @param trainerId Optional trainer ID (null for all trainers)
     * @param startDate Start date
     * @param endDate End date
     * @param format Format of the report
     * @return List of JSON-compatible maps with trainer data
     */
    // List<Map<String, Object>> generateTrainerReport(Optional<UUID> trainerId, LocalDate startDate, LocalDate endDate, String format);

    /**
     * Gets attendance statistics
     * @param startDate Start date
     * @param endDate End date
     * @return Map of statistics
     */
    Map<String, Integer> getAttendanceStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * Gets routine usage statistics
     * @param startDate Start date
     * @param endDate End date
     * @return Map of routine IDs to usage counts
     */
    // Map<UUID, Integer> getRoutineUsageStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * Gets progress statistics for a user
     * @param userId ID of the user
     * @param months Number of months to analyze
     * @return Map of statistics
     */
    // Map<String, Object> getUserProgressStatistics(UUID userId, int months);

    /**
     * Gets gym capacity utilization
     * @param startDate Start date
     * @param endDate End date
     * @param groupBy How to group data (hour, day, week)
     * @return Map of time periods to utilization percentages
     */
    Map<String, Double> getCapacityUtilization(LocalDate startDate, LocalDate endDate, String groupBy);
}