package com.AirlineBooking.AirlineBookig.repository;

import com.AirlineBooking.AirlineBookig.model.BookingStatus;
import com.AirlineBooking.AirlineBookig.model.CabinClass;
import com.AirlineBooking.AirlineBookig.model.Flight;
import com.AirlineBooking.AirlineBookig.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    /**
     * Find all seats for a specific flight
     */
    List<Seat> findByFlight(Flight flight);
    
    /**
     * Find all seats for a flight by flight ID
     */
    List<Seat> findByFlight_FlightId(Long flightId);
    
    /**
     * Find available seats for a specific flight
     */
    List<Seat> findByFlightAndBookingStatus(Flight flight, BookingStatus bookingStatus);
    
    /**
     * Find available seats by flight ID
     */
    @Query("SELECT s FROM Seat s WHERE s.flight.flightId = :flightId " +
           "AND s.bookingStatus = 'AVAILABLE'")
    List<Seat> findAvailableSeatsByFlightId(@Param("flightId") Long flightId);
    
    /**
     * Find seat by flight and seat number
     */
    Optional<Seat> findByFlightAndSeatNumber(Flight flight, String seatNumber);
    
    /**
     * Check if seat number exists for a flight
     */
    boolean existsByFlightAndSeatNumber(Flight flight, String seatNumber);
    
    /**
     * Count available seats for a flight
     */
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.flight.flightId = :flightId " +
           "AND s.bookingStatus = 'AVAILABLE'")
    long countAvailableSeatsByFlightId(@Param("flightId") Long flightId);
    
    /**
     * Find available seats by cabin class for a flight
     */
    List<Seat> findByFlightAndCabinClassAndBookingStatus(
        Flight flight, 
        CabinClass cabinClass, 
        BookingStatus bookingStatus
    );
    
    /**
     * Delete all seats for a specific flight
     * Used when flight is deleted
     */
    void deleteByFlight(Flight flight);
}
