package com.AirlineBooking.AirlineBookig.dto.response;

import com.AirlineBooking.AirlineBookig.model.BookingStatus;
import com.AirlineBooking.AirlineBookig.model.CabinClass;
import com.AirlineBooking.AirlineBookig.model.Seat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {
    
    private Long seatId;
    private String seatNumber;
    private CabinClass cabinClass;
    private BookingStatus bookingStatus;
    private Long flightId;
    private String flightNumber;
    
    /**
     * Convert Seat entity to SeatResponse DTO
     */
    public static SeatResponse fromEntity(Seat seat) {
        SeatResponse response = new SeatResponse();
        response.setSeatId(seat.getSeatId());
        response.setSeatNumber(seat.getSeatNumber());
        response.setCabinClass(seat.getCabinClass());
        response.setBookingStatus(seat.getBookingStatus());
        response.setFlightId(seat.getFlight().getFlightId());
        response.setFlightNumber(seat.getFlight().getFlightNumber());
        return response;
    }
}