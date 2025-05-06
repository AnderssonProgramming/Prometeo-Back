package edu.eci.cvds.prometeo.service;

import edu.eci.cvds.prometeo.dto.UserProfileDTO;
import edu.eci.cvds.prometeo.dto.UserProfileUpdateDTO;
import edu.eci.cvds.prometeo.model.User;

import java.util.List;

public interface UserService {
    
    /**
     * Get user by ID
     * @param id user ID
     * @return user entity
     */
    User getUserById(Long id);
    
    /**
     * Get user profile information
     * @param id user ID
     * @return user profile DTO
     */
    UserProfileDTO getUserProfile(Long id);
    
    /**
     * Update user profile information
     * @param id user ID
     * @param profileDTO profile data to update
     * @return updated profile
     */
    UserProfileDTO updateUserProfile(Long id, UserProfileUpdateDTO profileDTO);
    
    /**
     * Get list of users assigned to the current trainer
     * @return list of user profiles
     */
    List<UserProfileDTO> getTrainerAssignedUsers();
    
    /**
     * Assign a user to a trainer
     * @param userId user ID
     * @param trainerId trainer ID
     */
    void assignUserToTrainer(Long userId, Long trainerId);
}
