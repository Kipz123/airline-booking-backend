package com.AirlineBooking.AirlineBookig.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    
    @NotNull(message = "Flight ID is required")
    private Long flightId;
    
    @NotNull(message = "Seat ID is required")
    private Long seatId;
}