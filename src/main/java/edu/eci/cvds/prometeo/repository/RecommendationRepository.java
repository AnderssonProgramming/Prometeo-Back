package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
    List<Recommendation> findByUserIdAndActive(UUID userId, boolean active);
    Optional<Recommendation> findByUserIdAndRoutineId(UUID userId, UUID routineId);
}
