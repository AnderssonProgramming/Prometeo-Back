package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByUserIdAndActive(UUID userId, boolean active);
}
