package com.AirlineBooking.AirlineBookig.service;



import com.AirlineBooking.AirlineBookig.model.BookingStatus;
import com.AirlineBooking.AirlineBookig.model.Flight;
import com.AirlineBooking.AirlineBookig.model.Seat;
import com.AirlineBooking.AirlineBookig.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    @Autowired
    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    /**
     * Get all seats for a flight
     */
    public List<Seat> getSeatsByFlight(Long flightId) {
        return seatRepository.findByFlight_FlightId(flightId);
    }

    /**
     * Get available seats for a flight
     */
    public List<Seat> getAvailableSeats(Long flightId) {
        return seatRepository.findAvailableSeatsByFlightId(flightId);
    }

    /**
     * Get seat by ID
     */
    public Seat getSeatById(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));
    }

    /**
     * Check if seat is available
     */
    public boolean isSeatAvailable(Long seatId) {
        Seat seat = getSeatById(seatId);
        return seat.getBookingStatus() == BookingStatus.AVAILABLE;
    }

    /**
     * Reserve a seat (mark as RESERVED)
     */
    @Transactional
    public Seat reserveSeat(Long seatId) {
        Seat seat = getSeatById(seatId);

        if (seat.getBookingStatus() != BookingStatus.AVAILABLE) {
            throw new RuntimeException("Seat is not available");
        }

        seat.setBookingStatus(BookingStatus.RESERVED);
        return seatRepository.save(seat);
    }

    /**
     * Release a seat (mark as AVAILABLE)
     */
    @Transactional
    public Seat releaseSeat(Long seatId) {
        Seat seat = getSeatById(seatId);
        seat.setBookingStatus(BookingStatus.AVAILABLE);
        return seatRepository.save(seat);
    }

    /**
     * Mark seat as occupied
     */
    @Transactional
    public Seat occupySeat(Long seatId) {
        Seat seat = getSeatById(seatId);
        seat.setBookingStatus(BookingStatus.OCCUPIED);
        return seatRepository.save(seat);
    }

    /**
     * Count available seats for a flight
     */
    public long countAvailableSeats(Long flightId) {
        return seatRepository.countAvailableSeatsByFlightId(flightId);
    }
}