package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.GymSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GymSessionRepository extends JpaRepository<GymSession, UUID> {
    
    /**
     * Find sessions by date ordered by start time
     */
    List<GymSession> findBySessionDateOrderByStartTime(LocalDate date);
    
    /**
     * Find sessions by date and trainer ID
     */
    List<GymSession> findBySessionDateAndTrainerId(LocalDate date, UUID trainerId);
    
    /**
     * Find sessions by date range
     */
    List<GymSession> findBySessionDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find a session that covers the specified time slot
     */
    Optional<GymSession> findBySessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            LocalDate date, LocalTime startTime, LocalTime endTime);
    
    /**
     * Find sessions with available capacity
     */
    List<GymSession> findBySessionDateAndReservedSpotsLessThan(LocalDate date, int capacity);

    /**
     * Find sessions by date
     */
    List<GymSession> findBySessionDate(LocalDate date);
}