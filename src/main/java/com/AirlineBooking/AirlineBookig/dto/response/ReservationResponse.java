package com.AirlineBooking.AirlineBookig.dto.response;

import com.AirlineBooking.AirlineBookig.model.Reservation;
import com.AirlineBooking.AirlineBookig.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    
    private Long reservationId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long flightId;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Long seatId;
    private String seatNumber;
    private String cabinClass;
    private ReservationStatus status;
    private LocalDateTime reservationDate;
    
    /**
     * Convert Reservation entity to ReservationResponse DTO
     */
    public static ReservationResponse fromEntity(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        
        // Reservation details
        response.setReservationId(reservation.getReservationId());
        response.setStatus(reservation.getStatus());
        response.setReservationDate(reservation.getReservationDate());
        
        // User details
        response.setUserId(reservation.getUser().getUserId());
        response.setUserName(reservation.getUser().getName());
        response.setUserEmail(reservation.getUser().getEmail());
        
        // Flight details
        response.setFlightId(reservation.getFlight().getFlightId());
        response.setFlightNumber(reservation.getFlight().getFlightNumber());
        response.setOrigin(reservation.getFlight().getOrigin());
        response.setDestination(reservation.getFlight().getDestination());
        response.setDepartureTime(reservation.getFlight().getDepartureTime());
        response.setArrivalTime(reservation.getFlight().getArrivalTime());
        
        // Seat details
        response.setSeatId(reservation.getSeat().getSeatId());
        response.setSeatNumber(reservation.getSeat().getSeatNumber());
        response.setCabinClass(reservation.getSeat().getCabinClass().name());
        
        return response;
    }
}