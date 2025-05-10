package edu.eci.cvds.prometeo.model;

import edu.eci.cvds.prometeo.model.base.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entity representing a gym session
 */
@Entity
@Table(name = "gym_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GymSession extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "reserved_spots", nullable = false)
    private int reservedSpots;

    @Column(name = "trainer_id")
    private UUID trainerId;

    // Business logic methods
    public boolean hasAvailability() {
        return reservedSpots < capacity;
    }

    public int getAvailableSpots() {
        return capacity - reservedSpots;
    }

    public void reserve() {
        if (reservedSpots >= capacity) {
            throw new IllegalStateException("Session is at full capacity");
        }
        reservedSpots++;
    }

    public void cancelReservation() {
        if (reservedSpots > 0) {
            reservedSpots--;
        }
    }

    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getReservedSpots() {
        return reservedSpots;
    }

    public void setReservedSpots(int reservedSpots) {
        this.reservedSpots = reservedSpots;
    }

    public UUID getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(UUID trainerId) {
        this.trainerId = trainerId;
    }
}