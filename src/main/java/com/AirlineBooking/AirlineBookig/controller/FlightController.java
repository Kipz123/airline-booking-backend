package com.AirlineBooking.AirlineBookig.controller;

import com.AirlineBooking.AirlineBookig.dto.response.ApiResponse;
import com.AirlineBooking.AirlineBookig.dto.response.FlightResponse;
import com.AirlineBooking.AirlineBookig.dto.response.SeatResponse;
import com.AirlineBooking.AirlineBookig.model.Flight;
import com.AirlineBooking.AirlineBookig.model.Seat;
import com.AirlineBooking.AirlineBookig.service.FlightService;
import com.AirlineBooking.AirlineBookig.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightController {

    private final FlightService flightService;
    private final SeatService seatService;

    @Autowired
    public FlightController(FlightService flightService, SeatService seatService) {
        this.flightService = flightService;
        this.seatService = seatService;
    }

    /**
     * Get all flights
     * GET /api/flights
     */
    @GetMapping
    public ResponseEntity<?> getAllFlights() {
        try {
            List<Flight> flights = flightService.getAllFlights();
            List<FlightResponse> response = flights.stream()
                    .map(flight -> FlightResponse.fromEntity(
                            flight, 
                            flightService.getAvailableSeatCount(flight.getFlightId())
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Flights retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get flight by ID
     * GET /api/flights/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFlightById(@PathVariable Long id) {
        try {
            Flight flight = flightService.getFlightById(id);
            FlightResponse response = FlightResponse.fromEntity(
                    flight, 
                    flightService.getAvailableSeatCount(id)
            );
            return ResponseEntity.ok(ApiResponse.success("Flight retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Search flights by origin and destination
     * GET /api/flights/search?origin=Nairobi&destination=Mombasa
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchFlights(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination) {
        try {
            List<Flight> flights = flightService.searchFlights(origin, destination);
            List<FlightResponse> response = flights.stream()
                    .map(flight -> FlightResponse.fromEntity(
                            flight, 
                            flightService.getAvailableSeatCount(flight.getFlightId())
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Flights found", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get available flights (scheduled only)
     * GET /api/flights/available
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableFlights() {
        try {
            List<Flight> flights = flightService.getScheduledFlights();
            List<FlightResponse> response = flights.stream()
                    .map(flight -> FlightResponse.fromEntity(
                            flight, 
                            flightService.getAvailableSeatCount(flight.getFlightId())
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Available flights retrieved", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get seats for a specific flight
     * GET /api/flights/{id}/seats
     */
    @GetMapping("/{id}/seats")
    public ResponseEntity<?> getFlightSeats(@PathVariable Long id) {
        try {
            List<Seat> seats = seatService.getSeatsByFlight(id);
            List<SeatResponse> response = seats.stream()
                    .map(SeatResponse::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Seats retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get available seats for a flight
     * GET /api/flights/{id}/seats/available
     */
    @GetMapping("/{id}/seats/available")
    public ResponseEntity<?> getAvailableSeats(@PathVariable Long id) {
        try {
            List<Seat> seats = seatService.getAvailableSeats(id);
            List<SeatResponse> response = seats.stream()
                    .map(SeatResponse::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Available seats retrieved", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}