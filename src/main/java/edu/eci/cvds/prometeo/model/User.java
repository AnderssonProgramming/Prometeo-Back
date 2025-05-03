package edu.eci.cvds.prometeo.model;

import edu.eci.cvds.prometeo.model.base.AuditableEntity;
import edu.eci.cvds.prometeo.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends AuditableEntity {

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "identification_number", unique = true)
    private String identificationNumber;

    @Column(name = "identification_type")
    private String identificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "program_code")
    private String programCode;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "address")
    private String address;

    public boolean validatePassword(String password) {
        // Implement password validation logic
        return this.password.equals(password); // This is simplistic, should use a proper password encoder
    }
}