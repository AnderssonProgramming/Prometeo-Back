package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.model.Goal;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GoalService {
    List<Goal> getGoalsByUser(UUID userId);
    void addUserGoal(UUID userId, List<String> goals);
    void updateUserGoal(Map<UUID, String> updatedGoals);
    void deleteGoal(UUID goalId);
}
