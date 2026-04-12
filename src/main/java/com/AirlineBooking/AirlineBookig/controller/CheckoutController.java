package com.AirlineBooking.AirlineBookig.controller;

import com.AirlineBooking.AirlineBookig.dto.request.PaymentRequest;
import com.AirlineBooking.AirlineBookig.model.Reservation;
import com.AirlineBooking.AirlineBookig.model.User;
import com.AirlineBooking.AirlineBookig.service.AuthService;
import com.AirlineBooking.AirlineBookig.service.PdfTicketService;
import com.AirlineBooking.AirlineBookig.service.ReservationService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "*")
@Slf4j
public class CheckoutController {

    private final ReservationService reservationService;
    private final AuthService authService;
    private final PdfTicketService pdfTicketService;

    @Autowired
    public CheckoutController(ReservationService reservationService,
            AuthService authService,
            PdfTicketService pdfTicketService) {
        this.reservationService = reservationService;
        this.authService = authService;
        this.pdfTicketService = pdfTicketService;
    }

    /**
     * Process checkout/payment and generate PDF Ticket
     * POST /api/checkout/{reservationId}
     */
    @PostMapping("/{reservationId}")
    public ResponseEntity<byte[]> processCheckout(
            @PathVariable Long reservationId,
            @Valid @RequestBody PaymentRequest paymentRequest,
            Authentication authentication) {
        try {
            // Get authenticated user
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);

            log.info("Processing payment for reservation {} by user {}", reservationId, email);
            log.info("Payment Details: Method={}, Amount={}, TransactionID={}",
                    paymentRequest.getPaymentMethod(), paymentRequest.getAmount(), paymentRequest.getTransactionId());

            // 1. Mark reservation as paid
            Integer pointsToRedeem = paymentRequest.getPointsToRedeem();
            Reservation reservation = reservationService.checkoutReservation(reservationId, user.getUserId(),
                    pointsToRedeem);

            // 2. Generate PDF ticket
            byte[] pdfBytes = pdfTicketService.generateTicketPdf(reservation);

            // 3. Return PDF as response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // The frontend can specify the filename in the header this way
            headers.setContentDispositionFormData("attachment", "ticket_" + reservation.getReservationId() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error during checkout: {}", e.getMessage());
            // In a real application, you might want a DTO that wraps the error and byte
            // array.
            // For now, if there's an error, we return 400 Bad Request with empty body.
            // The frontend should check status code before downloading.
            return ResponseEntity.badRequest().build();
        }
    }
}
