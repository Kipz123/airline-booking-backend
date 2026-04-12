package com.AirlineBooking.AirlineBookig.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotNull(message = "Amount is required")
    private Double amount;

    @NotBlank(message = "Payment Method is required")
    private String paymentMethod;

    // Additional fields from the frontend payment report can be added here
    private Integer pointsToRedeem;
}
