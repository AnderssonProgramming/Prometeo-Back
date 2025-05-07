package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, UUID> {

    List<Routine> findByTrainerId(UUID trainerId);

    List<Routine> findByDifficulty(String difficulty);

    List<Routine> findByGoal(String goal);

    List<Routine> findByTrainerIdAndDeletedAtIsNull(UUID trainerId);

    @Query("SELECT r FROM Routine r JOIN UserRoutine ur ON r.id = ur.routineId WHERE ur.userId = :userId AND ur.active = true")
    Optional<Routine> findCurrentRoutineByUserId(@Param("userId") UUID userId);
}