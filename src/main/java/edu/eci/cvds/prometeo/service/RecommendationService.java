package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.model.Routine;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

/**
 * Service for providing personalized recommendations to users
 */
public interface RecommendationService {
    
    /**
     * Recommends routines for a user based on their profile and progress
     * @param userId ID of the user
     * @param goal Optional goal filter
     * @param limit Maximum number of recommendations
     * @return List of recommended routines with compatibility scores
     */
    List<Map<Routine, Integer>> recommendRoutines(UUID userId, Optional<String> goal, int limit);
    
    /**
     * Recommends optimal gym times based on user preferences and gym occupancy
     * @param userId ID of the user
     * @param date Date for recommendations
     * @return Map of time slots to occupancy percentages
     */
    Map<LocalTime, Integer> recommendTimeSlots(UUID userId, LocalDate date);
    
    /**
     * Finds similar users based on physical characteristics and goals
     * @param userId ID of the user
     * @param limit Maximum number of similar users to find
     * @return Map of user IDs to similarity scores
     */
    Map<UUID, Integer> findSimilarUsers(UUID userId, int limit);
    
    /**
     * Generates improvement suggestions based on user progress
     * @param userId ID of the user
     * @return List of suggestions
     */
    List<String> generateImprovementSuggestions(UUID userId);
    
    /**
     * Predicts user's progress based on current trends
     * @param userId ID of the user
     * @param weeksAhead Number of weeks to predict
     * @return Map of metrics to predicted values
     */
    Map<String, Double> predictProgress(UUID userId, int weeksAhead);
    
    /**
     * Evaluates effectiveness of a routine for a user
     * @param userId ID of the user
     * @param routineId ID of the routine
     * @return Effectiveness score (0-100)
     */
    int evaluateRoutineEffectiveness(UUID userId, UUID routineId);
}