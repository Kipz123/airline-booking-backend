package com.AirlineBooking.AirlineBookig.repository;

import com.AirlineBooking.AirlineBookig.model.Reservation;
import com.AirlineBooking.AirlineBookig.model.ReservationStatus;
import com.AirlineBooking.AirlineBookig.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    /**
     * Find all reservations for a specific user
     */
    List<Reservation> findByUser(User user);
    
    /**
     * Find all reservations by user ID
     */
    List<Reservation> findByUser_UserId(Long userId);
    
    /**
     * Find all confirmed reservations for a user
     */
    List<Reservation> findByUserAndStatus(User user, ReservationStatus status);
    
    /**
     * Find reservation by user ID and reservation ID
     * Used to verify ownership before cancellation
     */
    Optional<Reservation> findByReservationIdAndUser_UserId(Long reservationId, Long userId);
    
    /**
     * Find all reservations for a specific flight
     */
    List<Reservation> findByFlight_FlightId(Long flightId);
    
    /**
     * Find reservation by seat ID
     * Used to check if seat is already reserved
     */
    Optional<Reservation> findBySeat_SeatIdAndStatus(Long seatId, ReservationStatus status);
    
    /**
     * Check if user has active reservation for a specific seat
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Reservation r WHERE r.user.userId = :userId " +
           "AND r.seat.seatId = :seatId AND r.status = 'CONFIRMED'")
    boolean existsByUserIdAndSeatIdAndConfirmed(
        @Param("userId") Long userId, 
        @Param("seatId") Long seatId
    );
    
    /**
     * Get all confirmed reservations for a flight
     */
    @Query("SELECT r FROM Reservation r WHERE r.flight.flightId = :flightId " +
           "AND r.status = 'CONFIRMED'")
    List<Reservation> findConfirmedReservationsByFlightId(@Param("flightId") Long flightId);
    
    /**
     * Count confirmed reservations for a user
     */
    long countByUserAndStatus(User user, ReservationStatus status);
}
