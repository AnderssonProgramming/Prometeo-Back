package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationDTO {
    private UUID id;
    private UUID userId;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime scheduledTime;
    private LocalDateTime sentTime;
    private UUID relatedEntityId;
}
