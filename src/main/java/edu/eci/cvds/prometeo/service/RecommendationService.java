package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.model.Routine;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for providing personalized recommendations to users
 */
public interface RecommendationService {

    /**
     * Recommends routines for a user based on their profile and progress
     * @param userId ID of the user
     * @return List of recommended routines with compatibility scores
     */
    List<Map<Routine, Integer>> recommendRoutines(UUID userId);


    /**
     * Finds routines from user
     * @param userId ID of the user
     * @return List of user IDs to similarity scores
     */
    List<Routine> findUserRoutines(UUID userId);
}