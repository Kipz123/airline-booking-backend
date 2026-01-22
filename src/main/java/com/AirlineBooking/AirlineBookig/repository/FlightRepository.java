package com.AirlineBooking.AirlineBookig.repository;

import com.AirlineBooking.AirlineBookig.model.Flight;
import com.AirlineBooking.AirlineBookig.model.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    /**
     * Find flight by flight number
     */
    Optional<Flight> findByFlightNumber(String flightNumber);
    
    /**
     * Check if flight number already exists
     */
    boolean existsByFlightNumber(String flightNumber);
    
    /**
     * Find all flights by status
     */
    List<Flight> findByStatus(FlightStatus status);
    
    /**
     * Find flights by origin and destination
     */
    List<Flight> findByOriginAndDestination(String origin, String destination);
    
    /**
     * Find flights by origin, destination, and status
     */
    List<Flight> findByOriginAndDestinationAndStatus(
        String origin, 
        String destination, 
        FlightStatus status
    );
    
    /**
     * Find available flights (SCHEDULED status) between dates
     */
    @Query("SELECT f FROM Flight f WHERE f.status = :status " +
           "AND f.departureTime BETWEEN :startDate AND :endDate " +
           "ORDER BY f.departureTime ASC")
    List<Flight> findAvailableFlightsBetweenDates(
        @Param("status") FlightStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Search flights by origin and/or destination with available seats
     */
    @Query("SELECT f FROM Flight f WHERE " +
           "(LOWER(f.origin) LIKE LOWER(CONCAT('%', :origin, '%')) OR :origin IS NULL) " +
           "AND (LOWER(f.destination) LIKE LOWER(CONCAT('%', :destination, '%')) OR :destination IS NULL) " +
           "AND f.status = 'SCHEDULED' " +
           "AND EXISTS (SELECT s FROM Seat s WHERE s.flight = f AND s.bookingStatus = 'AVAILABLE') " +
           "ORDER BY f.departureTime ASC")
    List<Flight> searchAvailableFlights(
        @Param("origin") String origin,
        @Param("destination") String destination
    );
}