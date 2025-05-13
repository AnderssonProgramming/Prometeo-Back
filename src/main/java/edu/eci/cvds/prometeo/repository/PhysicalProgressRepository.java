package edu.eci.cvds.prometeo.repository;


import edu.eci.cvds.prometeo.model.PhysicalProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhysicalProgressRepository extends JpaRepository<PhysicalProgress, UUID> {
    
    List<PhysicalProgress> findByUserId(UUID userId);
    
    List<PhysicalProgress> findByUserIdOrderByRecordDateDesc(UUID userId);
    
    List<PhysicalProgress> findByUserIdAndRecordDateBetween(
            UUID userId, LocalDate startDate, LocalDate endDate);
}