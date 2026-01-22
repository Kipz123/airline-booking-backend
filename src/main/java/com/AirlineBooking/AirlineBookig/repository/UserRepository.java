package com.AirlineBooking.AirlineBookig.repository;

import com.AirlineBooking.AirlineBookig.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email address
     * Used for authentication and registration validation
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if email already exists in database
     * Used during user registration to prevent duplicate emails
     */
    boolean existsByEmail(String email);
}