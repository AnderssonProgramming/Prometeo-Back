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
    
    // TODO: Decide which roles are going to be used
    List<User> findByRole(UserRole role);
    
    Optional<User> findByUsername(String username);
    
    // TODO: Decide if this search is needed
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}