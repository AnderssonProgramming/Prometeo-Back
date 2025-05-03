package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.GymSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface GymSessionRepository extends JpaRepository<GymSession, UUID> {
    
    List<GymSession> findBySessionDateBetween(LocalDate start, LocalDate end);
    
    List<GymSession> findByTrainerIdAndSessionDate(UUID trainerId, LocalDate date);
    
    List<GymSession> findBySessionDateAndDeletedAtIsNull(LocalDate date);
    
    List<GymSession> findBySessionDateAfterAndDeletedAtIsNull(LocalDate date);
}