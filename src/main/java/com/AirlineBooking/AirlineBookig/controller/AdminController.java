package com.AirlineBooking.AirlineBookig.controller;

import com.AirlineBooking.AirlineBookig.dto.request.FlightRequest;
import com.AirlineBooking.AirlineBookig.dto.response.ApiResponse;
import com.AirlineBooking.AirlineBookig.dto.response.FlightResponse;
import com.AirlineBooking.AirlineBookig.dto.response.ReservationResponse;
import com.AirlineBooking.AirlineBookig.model.Flight;
import com.AirlineBooking.AirlineBookig.model.Reservation;
import com.AirlineBooking.AirlineBookig.service.FlightService;
import com.AirlineBooking.AirlineBookig.service.ReservationService;
import com.AirlineBooking.AirlineBookig.service.AnalyticsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FlightService flightService;
    private final ReservationService reservationService;
    private final AnalyticsService analyticsService;

    @Autowired
    public AdminController(FlightService flightService, ReservationService reservationService,
            AnalyticsService analyticsService) {
        this.flightService = flightService;
        this.reservationService = reservationService;
        this.analyticsService = analyticsService;
    }

    /**
     * Create a new flight
     * POST /api/admin/flights
     */
    @PostMapping("/flights")
    public ResponseEntity<?> createFlight(@Valid @RequestBody FlightRequest request) {
        try {
            Flight flight = flightService.createFlight(
                    request.getFlightNumber(),
                    request.getOrigin(),
                    request.getDestination(),
                    request.getDepartureTime(),
                    request.getArrivalTime(),
                    request.getSeatCapacity(),
                    request.getDistance());

            FlightResponse response = FlightResponse.fromEntity(
                    flight,
                    flightService.getAvailableSeatCount(flight.getFlightId()));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Flight created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update flight details
     * PUT /api/admin/flights/{id}
     */
    @PutMapping("/flights/{id}")
    public ResponseEntity<?> updateFlight(
            @PathVariable Long id,
            @RequestBody FlightRequest request) {
        try {
            Flight flight = flightService.updateFlight(
                    id,
                    request.getOrigin(),
                    request.getDestination(),
                    request.getDepartureTime(),
                    request.getArrivalTime());

            FlightResponse response = FlightResponse.fromEntity(
                    flight,
                    flightService.getAvailableSeatCount(id));

            return ResponseEntity.ok(ApiResponse.success("Flight updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Cancel a flight
     * PATCH /api/admin/flights/{id}/cancel
     */
    @PatchMapping("/flights/{id}/cancel")
    public ResponseEntity<?> cancelFlight(@PathVariable Long id) {
        try {
            Flight flight = flightService.cancelFlight(id);
            FlightResponse response = FlightResponse.fromEntity(flight);

            return ResponseEntity.ok(ApiResponse.success("Flight cancelled successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Delete a flight
     * DELETE /api/admin/flights/{id}
     */
    @DeleteMapping("/flights/{id}")
    public ResponseEntity<?> deleteFlight(@PathVariable Long id) {
        try {
            flightService.deleteFlight(id);
            return ResponseEntity.ok(ApiResponse.success("Flight deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get all reservations for a specific flight
     * GET /api/admin/flights/{id}/reservations
     */
    @GetMapping("/flights/{id}/reservations")
    public ResponseEntity<?> getFlightReservations(@PathVariable Long id) {
        try {
            List<Reservation> reservations = reservationService.getFlightReservations(id);
            List<ReservationResponse> response = reservations.stream()
                    .map(ReservationResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Reservations retrieved", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get all reservations (Admin view)
     * GET /api/admin/reservations
     */
    @GetMapping("/reservations")
    public ResponseEntity<?> getAllReservations() {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            List<ReservationResponse> response = reservations.stream()
                    .map(ReservationResponse::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Reservations retrieved", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get analytics data (revenue and activities)
     * GET /api/admin/analytics
     */
    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Analytics retrieved", analyticsService.getAnalytics()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}