package edu.eci.cvds.prometeo.repository;


import edu.eci.cvds.prometeo.model.PhysicalProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhysicalProgressRepository extends JpaRepository<PhysicalProgress, UUID> {
    
    List<PhysicalProgress> findByUserId(UUID userId);
    
    @Query("SELECT p FROM PhysicalProgress p WHERE p.userId = :userId ORDER BY p.recordDate DESC")
    List<PhysicalProgress> findByUserIdOrderByRecordDateDesc(UUID userId);
    
    @Query("SELECT p FROM PhysicalProgress p WHERE p.userId = :userId ORDER BY p.recordDate DESC LIMIT 1")
    Optional<PhysicalProgress> findLatestByUserId(UUID userId);
}