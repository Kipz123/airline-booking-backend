package com.AirlineBooking.AirlineBookig.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {

    private List<DailyRevenue> revenueData;
    private List<FlightRevenue> flightRevenueData;
    private List<ActivityLog> activities;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRevenue {
        private String date;
        private double revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityLog {
        private String date;
        private String time;
        private String description;
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlightRevenue {
        private String flight;
        private double revenue;
    }
}
