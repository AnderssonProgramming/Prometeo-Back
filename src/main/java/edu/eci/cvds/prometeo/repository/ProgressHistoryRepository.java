package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.ProgressHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProgressHistoryRepository extends JpaRepository<ProgressHistory, UUID> {
    
    List<ProgressHistory> findByUserId(UUID userId);
    
    List<ProgressHistory> findByUserIdAndMeasureType(UUID userId, String measureType);
    
    List<ProgressHistory> findByUserIdAndRecordDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);
    
    List<ProgressHistory> findByUserIdOrderByRecordDateDesc(UUID userId);
}