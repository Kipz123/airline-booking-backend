package com.AirlineBooking.AirlineBookig.service;



import com.AirlineBooking.AirlineBookig.model.*;
import com.AirlineBooking.AirlineBookig.repository.FlightRepository;
import com.AirlineBooking.AirlineBookig.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;

    @Autowired
    public FlightService(FlightRepository flightRepository, SeatRepository seatRepository) {
        this.flightRepository = flightRepository;
        this.seatRepository = seatRepository;
    }

    /**
     * Create a new flight with seats
     */
    @Transactional
    public Flight createFlight(String flightNumber, String origin, String destination,
                               LocalDateTime departureTime, LocalDateTime arrivalTime,
                               Integer seatCapacity) {
        // Check if flight number already exists
        if (flightRepository.existsByFlightNumber(flightNumber)) {
            throw new RuntimeException("Flight number already exists");
        }

        // Validate times
        if (arrivalTime.isBefore(departureTime)) {
            throw new RuntimeException("Arrival time must be after departure time");
        }

        // Create flight
        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setOrigin(origin);
        flight.setDestination(destination);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setSeatCapacity(seatCapacity);
        flight.setStatus(FlightStatus.SCHEDULED);

        Flight savedFlight = flightRepository.save(flight);

        // Generate seats for the flight
        generateSeatsForFlight(savedFlight, seatCapacity);

        return savedFlight;
    }

    /**
     * Generate seats for a flight
     */
    private void generateSeatsForFlight(Flight flight, Integer capacity) {
        List<Seat> seats = new ArrayList<>();

        // Generate seats (e.g., A1, A2, B1, B2, etc.)
        int seatsPerRow = 6; // A, B, C, D, E, F
        char[] seatLetters = {'A', 'B', 'C', 'D', 'E', 'F'};

        for (int i = 0; i < capacity; i++) {
            int row = (i / seatsPerRow) + 1;
            char letter = seatLetters[i % seatsPerRow];
            String seatNumber = letter + String.valueOf(row);

            Seat seat = new Seat();
            seat.setSeatNumber(seatNumber);
            seat.setFlight(flight);
            seat.setBookingStatus(BookingStatus.AVAILABLE);

            // Assign cabin class based on row
            if (row <= 2) {
                seat.setCabinClass(CabinClass.FIRST);
            } else if (row <= 5) {
                seat.setCabinClass(CabinClass.BUSINESS);
            } else {
                seat.setCabinClass(CabinClass.ECONOMY);
            }

            seats.add(seat);
        }

        seatRepository.saveAll(seats);
    }

    /**
     * Get all flights
     */
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    /**
     * Get flight by ID
     */
    public Flight getFlightById(Long flightId) {
        return flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
    }

    /**
     * Get flight by flight number
     */
    public Flight getFlightByNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
    }

    /**
     * Search available flights by origin and destination
     */
    public List<Flight> searchFlights(String origin, String destination) {
        return flightRepository.searchAvailableFlights(origin, destination);
    }

    /**
     * Get all scheduled flights
     */
    public List<Flight> getScheduledFlights() {
        return flightRepository.findByStatus(FlightStatus.SCHEDULED);
    }

    /**
     * Update flight details
     */
    @Transactional
    public Flight updateFlight(Long flightId, String origin, String destination,
                               LocalDateTime departureTime, LocalDateTime arrivalTime) {
        Flight flight = getFlightById(flightId);

        if (origin != null) flight.setOrigin(origin);
        if (destination != null) flight.setDestination(destination);
        if (departureTime != null) flight.setDepartureTime(departureTime);
        if (arrivalTime != null) flight.setArrivalTime(arrivalTime);

        // Validate times
        if (flight.getArrivalTime().isBefore(flight.getDepartureTime())) {
            throw new RuntimeException("Arrival time must be after departure time");
        }

        return flightRepository.save(flight);
    }

    /**
     * Cancel a flight
     */
    @Transactional
    public Flight cancelFlight(Long flightId) {
        Flight flight = getFlightById(flightId);
        flight.setStatus(FlightStatus.CANCELLED);
        return flightRepository.save(flight);
    }

    /**
     * Delete a flight
     */
    @Transactional
    public void deleteFlight(Long flightId) {
        Flight flight = getFlightById(flightId);
        flightRepository.delete(flight);
    }

    /**
     * Get available seat count for a flight
     */
    public long getAvailableSeatCount(Long flightId) {
        return seatRepository.countAvailableSeatsByFlightId(flightId);
    }

    /**
     * Check if flight has available seats
     */
    public boolean hasAvailableSeats(Long flightId) {
        return getAvailableSeatCount(flightId) > 0;
    }
}