package edu.eci.cvds.prometeo.repository;

import edu.eci.cvds.prometeo.model.User;
import edu.eci.cvds.prometeo.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // Optional<User> getUserById(UUID id);
    // Optional<User> getUser(String username);
    // TODO: To see if this is implicit
    List<User> findAll();
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