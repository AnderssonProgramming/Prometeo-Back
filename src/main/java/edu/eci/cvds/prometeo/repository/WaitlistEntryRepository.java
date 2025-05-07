package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.WaitlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WaitlistEntryRepository extends JpaRepository<WaitlistEntry, UUID> {
    
    /**
     * Find waitlist entries for a session
     */
    List<WaitlistEntry> findBySessionIdOrderByRequestDateAsc(UUID sessionId);
    
    /**
     * Find waitlist entries for a user and session
     */
    Optional<WaitlistEntry> findByUserIdAndSessionId(UUID userId, UUID sessionId);
    
    /**
     * Find not notified waitlist entries for a session
     */
    List<WaitlistEntry> findBySessionIdAndNotifiedFalseOrderByRequestDateAsc(UUID sessionId);
    
    /**
     * Find active waitlist entries for a user
     */
    List<WaitlistEntry> findByUserIdAndNotifiedFalse(UUID userId);
    
    /**
     * Delete waitlist entries for a session
     */
    void deleteBySessionId(UUID sessionId);
}