package edu.eci.cvds.prometeo.model;

import edu.eci.cvds.prometeo.model.base.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends AuditableEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "message", nullable = false)
    private String message;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Column(name = "read", nullable = false)
    private boolean read = false;
    
    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;
    
    @Column(name = "sent_time")
    private LocalDateTime sentTime;
    
    @Column(name = "related_entity_id")
    private UUID relatedEntityId;
    
    public void markAsRead() {
        this.read = true;
    }
    
    public boolean isScheduled() {
        return scheduledTime != null && sentTime == null;
    }
    
    public boolean isPending() {
        return scheduledTime == null && sentTime == null;
    }
    
    public boolean isSent() {
        return sentTime != null;
    }
}