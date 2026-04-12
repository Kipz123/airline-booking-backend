package com.AirlineBooking.AirlineBookig.service;

import com.AirlineBooking.AirlineBookig.dto.response.AnalyticsResponse;
import com.AirlineBooking.AirlineBookig.model.Flight;
import com.AirlineBooking.AirlineBookig.model.Reservation;
import com.AirlineBooking.AirlineBookig.repository.FlightRepository;
import com.AirlineBooking.AirlineBookig.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AnalyticsService {

    private final FlightRepository flightRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public AnalyticsService(FlightRepository flightRepository, ReservationRepository reservationRepository) {
        this.flightRepository = flightRepository;
        this.reservationRepository = reservationRepository;
    }

    public AnalyticsResponse getAnalytics() {
        List<Reservation> allReservations = reservationRepository.findAll();
        List<Flight> allFlights = flightRepository.findAll();

        Map<LocalDate, Double> revenueMap = new TreeMap<>(Collections.reverseOrder());
        Map<String, Double> flightRevMap = new HashMap<>();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        List<AnalyticsResponse.ActivityLog> activities = new ArrayList<>();

        for (Reservation res : allReservations) {
            double price = (res.getSeat() != null && res.getSeat().getPrice() != null) ? res.getSeat().getPrice() : 0.0;
            String statusName = res.getStatus().name();
            boolean isPaidBooking = "CONFIRMED".equals(statusName) || "PAID".equals(statusName)
                    || "REFUND_PENDING".equals(statusName) || "REFUNDED".equals(statusName);

            // 1. Calculate Revenue per Day (add paid bookings, deduct refunds)
            // Add on reservation date
            if (isPaidBooking && res.getReservationDate() != null) {
                LocalDate resDate = res.getReservationDate().toLocalDate();
                revenueMap.put(resDate, revenueMap.getOrDefault(resDate, 0.0) + price);
            }
            // Deduct on refund date if refunded
            if (("REFUND_PENDING".equals(statusName) || "REFUNDED".equals(statusName))
                    && res.getRefundRequestDate() != null) {
                LocalDate refundDate = res.getRefundRequestDate().toLocalDate();
                revenueMap.put(refundDate, revenueMap.getOrDefault(refundDate, 0.0) - price);
            }

            // 2. Calculate Revenue per Flight (only active paid bookings, exclude refunds)
            if ("CONFIRMED".equals(statusName) || "PAID".equals(statusName) || "COMPLETED".equals(statusName)) {
                String flightNo = res.getFlight().getFlightNumber() + " (" + res.getFlight().getOrigin() + " - "
                        + res.getFlight().getDestination() + ")";
                flightRevMap.put(flightNo, flightRevMap.getOrDefault(flightNo, 0.0) + price);
            }

            // 3. Generate detailed Activity Logs for bookings
            if (res.getReservationDate() != null) {
                LocalDate resDate = res.getReservationDate().toLocalDate();
                String statusToDisplay = statusName;

                // If flight departed and booking is active, display DEPARTED
                if (res.getFlight() != null && res.getFlight().getDepartureTime() != null
                        && res.getFlight().getDepartureTime().isBefore(LocalDateTime.now())
                        && ("CONFIRMED".equals(statusName) || "PAID".equals(statusName))) {
                    statusToDisplay = "DEPARTED";
                }

                activities.add(new AnalyticsResponse.ActivityLog(
                        resDate.format(dateFormatter),
                        res.getReservationDate().format(timeFormatter),
                        "Reservation for Flight " + res.getFlight().getFlightNumber() + " by " + res.getUser().getName()
                                + " (Seat " + res.getSeat().getSeatNumber() + ") - Status: " + statusToDisplay,
                        "BOOKING"));
            }

            // Log refund activity if applicable
            if (res.getRefundRequestDate() != null) {
                LocalDate refundDate = res.getRefundRequestDate().toLocalDate();
                activities.add(new AnalyticsResponse.ActivityLog(
                        refundDate.format(dateFormatter),
                        res.getRefundRequestDate().format(timeFormatter),
                        "Refund request for Flight " + res.getFlight().getFlightNumber() + " by "
                                + res.getUser().getName() + " - Amount: " + price,
                        "REFUND"));
            }
        }

        List<AnalyticsResponse.DailyRevenue> revenueData = new ArrayList<>();
        for (Map.Entry<LocalDate, Double> entry : revenueMap.entrySet()) {
            revenueData.add(new AnalyticsResponse.DailyRevenue(entry.getKey().format(dateFormatter), entry.getValue()));
        }

        List<AnalyticsResponse.FlightRevenue> flightRevenueData = new ArrayList<>();
        for (Map.Entry<String, Double> entry : flightRevMap.entrySet()) {
            // we sort this later or UI can sort it
            flightRevenueData.add(new AnalyticsResponse.FlightRevenue(entry.getKey(), entry.getValue()));
        }
        flightRevenueData.sort((a, b) -> Double.compare(b.getRevenue(), a.getRevenue()));

        // Flights created
        for (Flight f : allFlights) {
            if (f.getCreatedAt() != null) {
                LocalDate date = f.getCreatedAt().toLocalDate();
                activities.add(new AnalyticsResponse.ActivityLog(
                        date.format(dateFormatter),
                        f.getCreatedAt().format(timeFormatter),
                        "New Flight " + f.getFlightNumber() + " scheduled from " + f.getOrigin() + " to "
                                + f.getDestination(),
                        "FLIGHT_SCHEDULED"));
            }
        }

        // Sort activities by date and time descending
        activities.sort((a1, a2) -> {
            int dateCompare = a2.getDate().compareTo(a1.getDate());
            if (dateCompare != 0)
                return dateCompare;
            return a2.getTime().compareTo(a1.getTime());
        });

        return new AnalyticsResponse(revenueData, flightRevenueData, activities);
    }
}
