package com.AirlineBooking.AirlineBookig.dto.response;

import com.AirlineBooking.AirlineBookig.model.Flight;
import com.AirlineBooking.AirlineBookig.model.FlightStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponse {
    
    private Long flightId;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private FlightStatus status;
    private Integer seatCapacity;
    private Long availableSeats;
    private LocalDateTime createdAt;
    
    /**
     * Convert Flight entity to FlightResponse DTO
     */
    public static FlightResponse fromEntity(Flight flight, Long availableSeats) {
        FlightResponse response = new FlightResponse();
        response.setFlightId(flight.getFlightId());
        response.setFlightNumber(flight.getFlightNumber());
        response.setOrigin(flight.getOrigin());
        response.setDestination(flight.getDestination());
        response.setDepartureTime(flight.getDepartureTime());
        response.setArrivalTime(flight.getArrivalTime());
        response.setStatus(flight.getStatus());
        response.setSeatCapacity(flight.getSeatCapacity());
        response.setAvailableSeats(availableSeats);
        response.setCreatedAt(flight.getCreatedAt());
        return response;
    }
    
    /**
     * Convert Flight entity to FlightResponse DTO (without available seats count)
     */
    public static FlightResponse fromEntity(Flight flight) {
        FlightResponse response = new FlightResponse();
        response.setFlightId(flight.getFlightId());
        response.setFlightNumber(flight.getFlightNumber());
        response.setOrigin(flight.getOrigin());
        response.setDestination(flight.getDestination());
        response.setDepartureTime(flight.getDepartureTime());
        response.setArrivalTime(flight.getArrivalTime());
        response.setStatus(flight.getStatus());
        response.setSeatCapacity(flight.getSeatCapacity());
        response.setCreatedAt(flight.getCreatedAt());
        return response;
    }
}