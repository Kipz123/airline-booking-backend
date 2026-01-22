package com.AirlineBooking.AirlineBookig.controller;

import com.AirlineBooking.AirlineBookig.dto.request.ReservationRequest;
import com.AirlineBooking.AirlineBookig.dto.response.ApiResponse;
import com.AirlineBooking.AirlineBookig.dto.response.ReservationResponse;
import com.AirlineBooking.AirlineBookig.model.Reservation;
import com.AirlineBooking.AirlineBookig.model.User;
import com.AirlineBooking.AirlineBookig.service.AuthService;
import com.AirlineBooking.AirlineBookig.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;
    private final AuthService authService;

    @Autowired
    public ReservationController(ReservationService reservationService, AuthService authService) {
        this.reservationService = reservationService;
        this.authService = authService;
    }

    /**
     * Create a new reservation (Book a seat)
     * POST /api/reservations
     */
    @PostMapping
    public ResponseEntity<?> createReservation(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {
        try {
            // Get authenticated user
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);

            // Create reservation
            Reservation reservation = reservationService.createReservation(
                    user,
                    request.getFlightId(),
                    request.getSeatId()
            );

            ReservationResponse response = ReservationResponse.fromEntity(reservation);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Reservation created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get all reservations for authenticated user
     * GET /api/reservations/my-reservations
     */
    @GetMapping("/my-reservations")
    public ResponseEntity<?> getMyReservations(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);

            List<Reservation> reservations = reservationService.getUserReservations(user.getUserId());
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
     * Get reservation by ID
     * GET /api/reservations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);

            Reservation reservation = reservationService.getReservationByIdAndUserId(id, user.getUserId());
            ReservationResponse response = ReservationResponse.fromEntity(reservation);
            
            return ResponseEntity.ok(ApiResponse.success("Reservation retrieved", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Cancel a reservation
     * DELETE /api/reservations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);

            Reservation reservation = reservationService.cancelReservation(id, user.getUserId());
            ReservationResponse response = ReservationResponse.fromEntity(reservation);
            
            return ResponseEntity.ok(ApiResponse.success("Reservation cancelled successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}