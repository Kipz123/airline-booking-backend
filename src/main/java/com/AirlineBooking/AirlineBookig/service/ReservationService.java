package com.AirlineBooking.AirlineBookig.service;



import com.AirlineBooking.AirlineBookig.model.*;
import com.AirlineBooking.AirlineBookig.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatService seatService;
    private final FlightService flightService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository,
                              SeatService seatService,
                              FlightService flightService) {
        this.reservationRepository = reservationRepository;
        this.seatService = seatService;
        this.flightService = flightService;
    }

    /**
     * Create a new reservation (Book a seat)
     */
    @Transactional
    public Reservation createReservation(User user, Long flightId, Long seatId) {
        // Validate flight exists and is scheduled
        Flight flight = flightService.getFlightById(flightId);
        if (flight.getStatus() != FlightStatus.SCHEDULED) {
            throw new RuntimeException("Flight is not available for booking");
        }

        // Validate seat exists and is available
        Seat seat = seatService.getSeatById(seatId);
        if (!seatService.isSeatAvailable(seatId)) {
            throw new RuntimeException("Seat is not available");
        }

        // Verify seat belongs to the flight
        if (!seat.getFlight().getFlightId().equals(flightId)) {
            throw new RuntimeException("Seat does not belong to this flight");
        }

        // Reserve the seat
        seatService.reserveSeat(seatId);

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setFlight(flight);
        reservation.setSeat(seat);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        return reservationRepository.save(reservation);
    }

    /**
     * Get all reservations for a user
     */
    public List<Reservation> getUserReservations(Long userId) {
        return reservationRepository.findByUser_UserId(userId);
    }

    /**
     * Get confirmed reservations for a user
     */
    public List<Reservation> getUserConfirmedReservations(User user) {
        return reservationRepository.findByUserAndStatus(user, ReservationStatus.CONFIRMED);
    }

    /**
     * Get reservation by ID
     */
    public Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    /**
     * Get reservation by ID and verify user ownership
     */
    public Reservation getReservationByIdAndUserId(Long reservationId, Long userId) {
        return reservationRepository.findByReservationIdAndUser_UserId(reservationId, userId)
                .orElseThrow(() -> new RuntimeException("Reservation not found or unauthorized"));
    }

    /**
     * Cancel a reservation
     */
    @Transactional
    public Reservation cancelReservation(Long reservationId, Long userId) {
        // Get reservation and verify ownership
        Reservation reservation = getReservationByIdAndUserId(reservationId, userId);

        // Check if already cancelled
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Reservation is already cancelled");
        }

        // Update reservation status
        reservation.setStatus(ReservationStatus.CANCELLED);

        // Release the seat
        seatService.releaseSeat(reservation.getSeat().getSeatId());

        return reservationRepository.save(reservation);
    }

    /**
     * Get all reservations for a flight
     */
    public List<Reservation> getFlightReservations(Long flightId) {
        return reservationRepository.findByFlight_FlightId(flightId);
    }

    /**
     * Get confirmed reservations for a flight
     */
    public List<Reservation> getFlightConfirmedReservations(Long flightId) {
        return reservationRepository.findConfirmedReservationsByFlightId(flightId);
    }

    /**
     * Check if user has reservation for a specific seat
     */
    public boolean hasReservationForSeat(Long userId, Long seatId) {
        return reservationRepository.existsByUserIdAndSeatIdAndConfirmed(userId, seatId);
    }

    /**
     * Count user's confirmed reservations
     */
    public long countUserConfirmedReservations(User user) {
        return reservationRepository.countByUserAndStatus(user, ReservationStatus.CONFIRMED);
    }

    /**
     * Delete a reservation (Admin only)
     */
    @Transactional
    public void deleteReservation(Long reservationId) {
        Reservation reservation = getReservationById(reservationId);
        
        // Release seat if reservation was confirmed
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            seatService.releaseSeat(reservation.getSeat().getSeatId());
        }
        
        reservationRepository.delete(reservation);
    }
}