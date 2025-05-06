package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.PrometeoExceptions;
import edu.eci.cvds.prometeo.dto.UserProfileDTO;
import edu.eci.cvds.prometeo.dto.UserProfileUpdateDTO;
import edu.eci.cvds.prometeo.model.User;
import edu.eci.cvds.prometeo.model.UserTrainerAssignment;
import edu.eci.cvds.prometeo.repository.UserRepository;
import edu.eci.cvds.prometeo.repository.UserTrainerAssignmentRepository;
import edu.eci.cvds.prometeo.service.UserService;
import edu.eci.cvds.prometeo.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserTrainerAssignmentRepository userTrainerAssignmentRepository;
    private final SecurityService securityService;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            UserTrainerAssignmentRepository userTrainerAssignmentRepository,
            SecurityService securityService) {
        this.userRepository = userRepository;
        this.userTrainerAssignmentRepository = userTrainerAssignmentRepository;
        this.securityService = securityService;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.USUARIO_NO_ENCONTRADO));
    }

    @Override
    public UserProfileDTO getUserProfile(Long id) {
        User user = getUserById(id);
        
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setId(user.getId());
        profileDTO.setName(user.getName());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setPhoneNumber(user.getPhoneNumber());
        profileDTO.setMembershipType(user.getMembershipType());
        profileDTO.setRegistrationDate(user.getRegistrationDate());
        profileDTO.setProfilePictureUrl(user.getProfilePictureUrl());
        
        // If it's a regular user, get their assigned trainer
        if (!user.isTrainer() && !user.isAdmin()) {
            userTrainerAssignmentRepository.findByUserId(id)
                    .ifPresent(assignment -> {
                        User trainer = userRepository.findById(assignment.getTrainerId())
                                .orElse(null);
                        if (trainer != null) {
                            profileDTO.setAssignedTrainerId(trainer.getId());
                            profileDTO.setAssignedTrainerName(trainer.getName());
                        }
                    });
        }
        
        return profileDTO;
    }

    @Override
    @Transactional
    public UserProfileDTO updateUserProfile(Long id, UserProfileUpdateDTO profileDTO) {
        User user = getUserById(id);
        
        // Validate current user has permission to update this profile
        if (!securityService.isResourceOwner(id) && !securityService.hasAdminRole()) {
            throw new PrometeoExceptions(PrometeoExceptions.USUARIO_NO_AUTORIZADO);
        }
        
        // Update user fields
        if (profileDTO.getName() != null) {
            user.setName(profileDTO.getName());
        }
        
        if (profileDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(profileDTO.getPhoneNumber());
        }
        
        if (profileDTO.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(profileDTO.getProfilePictureUrl());
        }
        
        // Save updated user
        userRepository.save(user);
        
        // Return updated profile
        return getUserProfile(id);
    }

    @Override
    public List<UserProfileDTO> getTrainerAssignedUsers() {
        // Get current user (trainer) ID
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User trainer = userRepository.findByEmail(username)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.USUARIO_NO_ENCONTRADO));
        
        // Verify user is a trainer
        if (!trainer.isTrainer()) {
            throw new PrometeoExceptions(PrometeoExceptions.USUARIO_NO_ES_ENTRENADOR);
        }
        
        // Get all users assigned to this trainer
        List<UserTrainerAssignment> assignments = userTrainerAssignmentRepository.findByTrainerId(trainer.getId());
        
        // Convert to list of user profile DTOs
        return assignments.stream()
                .map(assignment -> getUserProfile(assignment.getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignUserToTrainer(Long userId, Long trainerId) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.USUARIO_NO_ENCONTRADO));
        
        // Validate trainer exists and is actually a trainer
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new PrometeoExceptions(PrometeoExceptions.USUARIO_NO_ENCONTRADO));
        
        if (!trainer.isTrainer()) {
            throw new PrometeoExceptions(PrometeoExceptions.USUARIO_NO_ES_ENTRENADOR);
        }
        
        // Remove any existing trainer assignment for this user
        userTrainerAssignmentRepository.findByUserId(userId)
                .ifPresent(assignment -> userTrainerAssignmentRepository.delete(assignment));
        
        // Create new assignment
        UserTrainerAssignment assignment = new UserTrainerAssignment();
        assignment.setUserId(userId);
        assignment.setTrainerId(trainerId);
        assignment.setAssignmentDate(LocalDateTime.now());
        
        userTrainerAssignmentRepository.save(assignment);
    }
}
