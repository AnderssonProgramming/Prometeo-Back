package edu.eci.cvds.prometeo.model;


import edu.eci.cvds.prometeo.model.base.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "gym_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GymSession extends AuditableEntity {

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    @Column(name = "current_capacity", nullable = false)
    private int currentCapacity;

    @Column(name = "trainer_id")
    private UUID trainerId;

    public boolean hasAvailability() {
        return currentCapacity < maxCapacity;
    }

    public boolean reserve() {
        if (hasAvailability()) {
            currentCapacity++;
            return true;
        }
        return false;
    }

    public boolean cancelReservation() {
        if (currentCapacity > 0) {
            currentCapacity--;
            return true;
        }
        return false;
    }

    public int getAvailableSpots() {
        return maxCapacity - currentCapacity;
    }

    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }
}