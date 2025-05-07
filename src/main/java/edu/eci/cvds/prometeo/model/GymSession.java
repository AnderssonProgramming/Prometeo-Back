package edu.eci.cvds.prometeo.model;

import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@Table(name = "gym_sessions")
public class GymSession {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "capacity", nullable = false)
    private int capacity;
    
    @Column(name = "current_bookings", nullable = false)
    private int currentBookings = 0;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE"; // ACTIVE, CANCELLED
    
    @Column(name = "trainer_id", nullable = false)
    private UUID trainerId;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    public boolean hasAvailability() {
        return "ACTIVE".equals(status) && currentBookings < capacity;
    }
    
    public void reserve() {
        if (currentBookings >= capacity) {
            throw new IllegalStateException("La sesión está a capacidad máxima");
        }
        currentBookings++;
    }
    
    public void cancelReservation() {
        if (currentBookings > 0) {
            currentBookings--;
        }
    }
    
    public int getAvailableSpots() {
        return capacity - currentBookings;
    }

    public int getCurrentBookings() {
        return currentBookings;
    }
}