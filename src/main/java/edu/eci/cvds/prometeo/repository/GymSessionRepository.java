package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.GymSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface GymSessionRepository extends JpaRepository<GymSession, UUID> {
    
    List<GymSession> findBySessionDateOrderByStartTimeAsc(LocalDate date);
    
    List<GymSession> findByTrainerIdOrderBySessionDateAscStartTimeAsc(UUID trainerId);
    
    List<GymSession> findByTrainerIdAndSessionDate(UUID trainerId, LocalDate date);
    
    List<GymSession> findByTrainerIdAndSessionDateBetween(UUID trainerId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT s FROM GymSession s WHERE s.sessionDate = :date AND s.status = 'ACTIVE' AND s.currentBookings < s.capacity ORDER BY s.startTime")
    List<GymSession> findAvailableSessionsByDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.sessionId = :sessionId AND r.attended = true")
    int countAttendanceBySessionId(@Param("sessionId") UUID sessionId);
}