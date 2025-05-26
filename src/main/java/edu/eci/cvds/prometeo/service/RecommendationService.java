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
     * Generates personalized routine recommendations for a specific user.
     *
     * @param userId the unique identifier of the user
     */
    List<Map<Routine, Integer>> recommendRoutines(UUID userId);


    /**
     * Retrieves the list of routines associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a list of the user's routines
     */
    List<Routine> findUserRoutines(UUID userId);
}