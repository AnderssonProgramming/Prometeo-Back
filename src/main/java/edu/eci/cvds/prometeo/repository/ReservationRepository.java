package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.Reservation;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    
    List<Reservation> findByUserId(UUID userId);
    
    List<Reservation> findBySessionId(UUID sessionId);
    
    List<Reservation> findByUserIdAndStatus(UUID userId, ReservationStatus status);
    
    List<Reservation> findBySessionIdAndStatus(UUID sessionId, ReservationStatus status);
    
    List<Reservation> findBySessionIdAndUserId(UUID sessionId, UUID userId);
    
    Long countBySessionId(UUID sessionId);
    
    Long countBySessionIdAndStatus(UUID sessionId, ReservationStatus status);
}