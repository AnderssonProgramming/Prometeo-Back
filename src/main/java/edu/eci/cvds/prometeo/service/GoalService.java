package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.model.Goal;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing user goals.
 * Provides methods to retrieve, add, update, and delete user-defined goals.
 */
public interface GoalService {
    /**
     * Retrieves all goals associated with a specific user.
     *
     * @param userId The unique identifier of the user.
     * @return A list of goals belonging to the user.
     */
    List<Goal> getGoalsByUser(UUID userId);

    /**
     * Adds new goals to the specified user.
     *
     * @param userId The unique identifier of the user.
     * @param goals  A list of goal descriptions to be added.
     */
    void addUserGoal(UUID userId, List<String> goals);

    /**
     * Updates the descriptions of existing goals.
     *
     * @param updatedGoals A map where the key is the goal ID and the value is the new goal description.
     */
    void updateUserGoal(Map<UUID, String> updatedGoals);

    /**
     * Deletes a goal by its unique identifier.
     *
     * @param goalId The unique identifier of the goal to be deleted.
     */
    void deleteGoal(UUID goalId);
}
