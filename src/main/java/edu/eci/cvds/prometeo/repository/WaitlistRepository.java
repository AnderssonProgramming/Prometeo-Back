package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.WaitlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaitlistRepository extends JpaRepository<WaitlistEntry, UUID> {
    
    List<WaitlistEntry> findBySessionIdOrderByRequestTimeAsc(UUID sessionId);
    
    List<WaitlistEntry> findByUserIdAndSessionId(UUID userId, UUID sessionId);
    
    List<WaitlistEntry> findByUserIdAndNotificationSentFalse(UUID userId);
    
    Optional<WaitlistEntry> findFirstBySessionIdAndNotificationSentFalseOrderByRequestTimeAsc(UUID sessionId);
    
    long countBySessionId(UUID sessionId);
}