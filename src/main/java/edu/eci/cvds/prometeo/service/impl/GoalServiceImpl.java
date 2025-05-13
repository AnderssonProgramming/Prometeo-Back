package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.model.Goal;
import edu.eci.cvds.prometeo.model.Recommendation;
import edu.eci.cvds.prometeo.repository.GoalRepository;
import edu.eci.cvds.prometeo.repository.RecommendationRepository;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.service.GoalService;
import edu.eci.cvds.prometeo.service.RecommendationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Implementation of the {@link GoalService} interface.
 * Handles the creation, update, retrieval, and soft deletion of user goals,
 * and manages the regeneration of routine recommendations accordingly.
 */
@Service
public class GoalServiceImpl implements GoalService {
    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private RecommendationService recommendationService;

    /**
     * Retrieves all active goals for a specific user.
     *
     * @param userId The UUID of the user.
     * @return A list of the user's active goals.
     */
    @Override
    public List<Goal> getGoalsByUser(UUID userId) {
        return goalRepository.findByUserIdAndActive(userId, true);
    }

    /**
     * Adds new goals to the specified user and regenerates recommendations.
     * Existing recommendations are deactivated before new ones are generated.
     *
     * @param userId The UUID of the user.
     * @param goals  A list of goal descriptions to add.
     */
    @Override
    public void addUserGoal(UUID userId, List<String> goals) {
        userRepository.findById(userId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_USUARIO));

        List<Recommendation> recommendations = recommendationRepository.findByUserIdAndActive(userId, true);
        recommendations.forEach(r -> r.setActive(false));
        recommendationRepository.saveAll(recommendations);

        goals.forEach(goalText -> {
            Goal goal = new Goal();
            goal.setUserId(userId);
            goal.setGoal(goalText);
            goal.setActive(true);
            goalRepository.save(goal);
        });

        recommendationService.recommendRoutines(userId);
    }

    /**
     * Updates the text of existing user goals and regenerates recommendations.
     * All current recommendations are deactivated and refreshed.
     *
     * @param updatedGoals A map of goal IDs and their new descriptions.
     */
    @Transactional
    @Override
    public void updateUserGoal(Map<UUID, String> updatedGoals) {
        if (updatedGoals.isEmpty()) return;

        UUID anyGoalId = updatedGoals.keySet().iterator().next();
        Goal referenceGoal = goalRepository.findById(anyGoalId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_META));
        UUID userId = referenceGoal.getUserId();

        for (Map.Entry<UUID, String> entry : updatedGoals.entrySet()) {
            UUID goalId = entry.getKey();
            String newText = entry.getValue();

            Goal goal = goalRepository.findById(goalId)
                    .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_META));
            goal.setGoal(newText);
            goalRepository.save(goal);
        }

        List<Recommendation> recommendations = recommendationRepository.findByUserIdAndActive(userId, true);
        recommendations.forEach(r -> r.setActive(false));
        recommendationRepository.saveAll(recommendations);

        recommendationService.recommendRoutines(userId);
    }

    /**
     * Soft deletes a goal by setting its active flag to false.
     * Also deactivates existing recommendations and generates new ones.
     *
     * @param goalId The UUID of the goal to delete.
     */
    @Override
    public void deleteGoal(UUID goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.NO_EXISTE_META));
        goal.setActive(false);
        goalRepository.save(goal);

        UUID userId = goal.getUserId();

        List<Recommendation> recommendations = recommendationRepository.findByUserIdAndActive(userId, true);
        recommendations.forEach(r -> r.setActive(false));
        recommendationRepository.saveAll(recommendations);

        recommendationService.recommendRoutines(userId);
    }
}
