package edu.eci.cvds.prometeo.model;

import edu.eci.cvds.prometeo.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_routines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoutine extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "routine_id", nullable = false)
    private UUID routineId;

    @Column(name = "assignment_date", nullable = false)
    private LocalDate assignmentDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void extend(int days) {
        if (this.endDate != null) {
            this.endDate = this.endDate.plusDays(days);
        }
    }
}