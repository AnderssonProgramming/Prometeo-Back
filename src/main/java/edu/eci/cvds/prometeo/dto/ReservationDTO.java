package edu.eci.cvds.prometeo.dto;

import edu.eci.cvds.prometeo.model.enums.ReservationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ReservationDTO {
    private UUID id;
    private UUID userId;
    private String userName;
    private String userEmail;
    private String userRole;
    private UUID sessionId;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private UUID trainerId;
    private String trainerName;
    private ReservationStatus status;
    private LocalDateTime reservationDate;
    private boolean attended;
    private List<EquipmentDTO> equipment;

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}