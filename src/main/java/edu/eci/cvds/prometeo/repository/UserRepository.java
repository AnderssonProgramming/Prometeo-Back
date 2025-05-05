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
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find users by trainer status
     */
    List<User> findByIsTrainer(boolean isTrainer);
    
    /**
     * Find users by program code
     */
    List<User> findByProgramCode(String programCode);
    
    /**
     * Check if a user exists by email
     */
    boolean existsByEmail(String email);
}