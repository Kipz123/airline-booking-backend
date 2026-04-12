package com.AirlineBooking.AirlineBookig.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RefundScheduler {

    private final ReservationService reservationService;

    @Autowired
    public RefundScheduler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Runs every 1 minute (60000 ms) to process pending refunds.
     */
    @Scheduled(fixedRate = 60000)
    public void processRefunds() {
        log.info("Running scheduled task: processing pending refunds...");
        try {
            reservationService.processPendingRefunds();
            log.info("Successfully processed pending refunds.");
        } catch (Exception e) {
            log.error("Error processing pending refunds: {}", e.getMessage(), e);
        }
    }
}
