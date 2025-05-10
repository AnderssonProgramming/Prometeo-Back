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

    @Column(name = "attendace_time")
    private LocalDateTime attendanceTime;

    @Column(name = "notes")
    private String notes;

    public LocalDateTime getCancellationDate() {
        return canceledAt;
    }
    public LocalDateTime getCheckInTime() {
        return attendanceTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.attendanceTime = checkInTime;
    }
    public void setCancellationDate(LocalDateTime cancellationDate) {
        this.canceledAt = cancellationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = ReservationStatus.valueOf(status);
    }

    public List<UUID> getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(List<UUID> equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public Boolean getAttended() {
        return attended;
    }

    public void setAttended(Boolean attended) {
        this.attended = attended;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public UUID getCompletedById() {
        return completedById;
    }

    public void setCompletedById(UUID completedById) {
        this.completedById = completedById;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    // Utility methods
    public LocalDate getDate() {
        return reservationDate.toLocalDate();
    }

    public LocalTime getStartTime() {
        return reservationDate.toLocalTime();
    }

    public LocalTime getEndTime() {
        // Assuming a default session length of 1 hour if not specified elsewhere
        return reservationDate.toLocalTime().plusHours(1);
    }

    public void setDate(LocalDate date) {
        this.reservationDate = LocalDateTime.of(date, reservationDate.toLocalTime());
    }

    public void setStartTime(LocalTime startTime) {
        this.reservationDate = LocalDateTime.of(reservationDate.toLocalDate(), startTime);
    }

    public void setEndTime(LocalTime endTime) {
        // This is only used to store the end time metadata, actual end time is derived from session
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

    public LocalDateTime getAttendanceTime() {
        return attendanceTime;
    }

    public void setAttendanceTime(LocalDateTime attendanceTime) {
        this.attendanceTime = attendanceTime;
    }
}