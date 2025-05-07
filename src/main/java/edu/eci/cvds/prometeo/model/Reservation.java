package edu.eci.cvds.prometeo.model;

import edu.eci.cvds.prometeo.model.base.AuditableEntity;
import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a gym reservation
 */
@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "reservation_date", nullable = false)
    private LocalDateTime reservationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @ElementCollection
    @CollectionTable(name = "reservation_equipment", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "equipment_id")
    private List<UUID> equipmentIds;

    @Column(name = "attended")
    private Boolean attended;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "completed_by_id")
    private UUID completedById;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;
    
    @Column(name = "waitlist_notification_requested")
    private boolean waitlistNotificationRequested = false;

    // Utility methods
    public LocalDate getDate() {
        return reservationDate.toLocalDate();
    }

    public LocalTime getStartTime() {
        return reservationDate.toLocalTime();
    }

    public LocalTime getEndTime() {
        // Assuming a default session length if not explicitly stored
        return reservationDate.toLocalTime().plusHours(1);
    }

    public void setDate(LocalDate date) {
        this.reservationDate = LocalDateTime.of(date, reservationDate.toLocalTime());
    }

    public void setStartTime(LocalTime startTime) {
        this.reservationDate = LocalDateTime.of(reservationDate.toLocalDate(), startTime);
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        this.canceledAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = ReservationStatus.COMPLETED;
    }

    public boolean isActive() {
        return this.status == ReservationStatus.CONFIRMED || this.status == ReservationStatus.PENDING;
    }

    public boolean isAttended() {
        return this.attended != null && this.attended;
    }
}