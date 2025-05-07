package edu.eci.cvds.prometeo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
public class ReservationDTO {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<UUID> equipmentIds;
    private Boolean joinWaitlistIfFull = false;

    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
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
    
    public List<UUID> getEquipmentIds() {
        return equipmentIds;
    }
    
    public void setEquipmentIds(List<UUID> equipmentIds) {
        this.equipmentIds = equipmentIds;
    }
    
    public Boolean getJoinWaitlistIfFull() {
        return joinWaitlistIfFull;
    }
    
    public void setJoinWaitlistIfFull(Boolean joinWaitlistIfFull) {
        this.joinWaitlistIfFull = joinWaitlistIfFull;
    }
}