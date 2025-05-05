package edu.eci.cvds.prometeo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private Double weight;
    private Double height;
    private Boolean isTrainer;
    private String programCode;
}
