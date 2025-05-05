package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ReservationDTO {
    private UUID id;
    private UUID userId;
    private UUID sessionId;
    private LocalDateTime reservationDate;
    private String status;
    private List<UUID> equipmentIds;
    private Boolean attended;
    private String cancellationReason;
    private UUID completedById;
    private LocalDateTime completedAt;
    private LocalDateTime canceledAt;
}
