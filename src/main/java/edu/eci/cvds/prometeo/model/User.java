package edu.eci.cvds.prometeo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

import edu.eci.cvds.prometeo.model.base.BaseEntity;

/**
 * Entity representing a user in the system
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "instutional_id", unique = true, nullable = false)
    private String institutionalId;

    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "weight")
    private Double weight;
    
    @Column(name = "height")
    private Double height;
    
    @Column(name = "is_trainer")
    private String role;
    
    
    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getRole() {
        return role;
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