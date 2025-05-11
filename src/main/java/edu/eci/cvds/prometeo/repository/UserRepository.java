package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Finds all users in the system.
     */
    List<User> findAll();

    /**
     * Finds a user by their role.
     * @param role the role of the user
     * @return a list of users with the specified role
     */
    List<User> findByRole(String role);
    Optional<User> findByInstitutionalId(String institutionalId);

    /**
     * Finds all users assigned to a specific trainer.
     * 
     * @param trainerId the UUID of the trainer
     * @return a list of users associated with the given trainer
     */
    // List<User> findByTrainerId(UUID trainerId);
}