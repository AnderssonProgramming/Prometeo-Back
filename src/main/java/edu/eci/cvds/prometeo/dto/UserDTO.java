package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String name;
    private Double weight;
    private Double height;
    private String role;
    private String institutionalId;
    // Getters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getHeight() {
        return height;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public String getInstitutionalId() {
        return institutionalId;
    }
    public void setInstitutionalId(String institutionalId) {
        this.institutionalId = institutionalId;
    }
}
