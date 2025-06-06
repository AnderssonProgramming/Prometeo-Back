package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.UserRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoutineRepository extends JpaRepository<UserRoutine, UUID> {
    List<UserRoutine> findByUserId(UUID userId);
    List<UserRoutine> findByUserIdAndActiveTrue(UUID userId);
    Optional<UserRoutine> findByUserIdAndRoutineId(UUID userId, UUID routineId);

}